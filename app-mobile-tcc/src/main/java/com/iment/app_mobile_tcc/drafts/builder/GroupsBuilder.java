package com.iment.app_mobile_tcc.drafts.builder;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.GroupRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.GroupsDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.ItemRequest;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlot;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlotContent;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class GroupsBuilder {

    private static final int MIN_GROUPS = 2;

    private static final int MAX_GROUPS = 3;

    private static final int MAX_ITEMS_PER_GROUP = 4;

    private static final int MAX_ITEMS = 9;

    private static final int MAX_GROUP_NAME = 14;

    private static final int MAX_ITEM_NAME = 18;

    private static final int MAX_QUESTION = 60;

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    @Autowired
    private ObjectMapper objectMapper;

    public QuestionRequest buildQuestion(GroupsDraftRequest obj){
        this.validate(obj);

        List<QuestionSlot> lstSlot = obj.groups().stream()
                .map(group -> new QuestionSlot(this.slug(group.name()), true, group.name().trim().toUpperCase(PT_BR)))
                .toList();

        JsonNode content = this.objectMapper.valueToTree(new QuestionSlotContent(lstSlot, null));

        return new QuestionRequest(obj.question().trim(), 1, null, QuestionTypeEnum.DRAG_SLOTS_TO_GROUP, content, null);
    }

    public List<AlternativeRequest> buildPieces(GroupsDraftRequest obj){
        this.validate(obj);

        List<AlternativeRequest> pieces = new ArrayList<>();

        for(GroupRequest group : obj.groups())
            for(ItemRequest item : group.items())
                pieces.add(new AlternativeRequest(item.name().trim(), true, null, this.slug(group.name()), item.emoji().trim()));

        Collections.shuffle(pieces);

        return pieces;
    }

    private void validate(GroupsDraftRequest obj){
        if(obj.question() == null || obj.question().isBlank() || obj.question().trim().length() > MAX_QUESTION)
            throw new RuntimeException("A pergunta precisa ter de 1 a " + MAX_QUESTION + " caracteres");

        if(obj.groups() == null || obj.groups().size() < MIN_GROUPS || obj.groups().size() > MAX_GROUPS)
            throw new RuntimeException("A atividade precisa ter de " + MIN_GROUPS + " a " + MAX_GROUPS + " grupos");

        Set<String> groupNames = new HashSet<>();
        Set<String> itemNames = new HashSet<>();
        int totalItems = 0;

        for(GroupRequest group : obj.groups()){
            if(group.name() == null || group.name().isBlank() || group.name().trim().length() > MAX_GROUP_NAME)
                throw new RuntimeException("O nome do grupo precisa ter de 1 a " + MAX_GROUP_NAME + " caracteres");

            if(this.slug(group.name()).isEmpty() || !groupNames.add(this.slug(group.name())))
                throw new RuntimeException("Grupo repetido ou sem nome: " + group.name());

            if(group.items() == null || group.items().isEmpty() || group.items().size() > MAX_ITEMS_PER_GROUP)
                throw new RuntimeException("O grupo " + group.name() + " precisa ter de 1 a " + MAX_ITEMS_PER_GROUP + " itens");

            for(ItemRequest item : group.items()){
                if(item.name() == null || item.name().isBlank() || item.name().trim().length() > MAX_ITEM_NAME)
                    throw new RuntimeException("O nome do item precisa ter de 1 a " + MAX_ITEM_NAME + " caracteres");

                if(item.emoji() == null || item.emoji().isBlank())
                    throw new RuntimeException("O item " + item.name() + " precisa de um emoji");

                if(!itemNames.add(this.slug(item.name())))
                    throw new RuntimeException("Item repetido: " + item.name());

                totalItems++;
            }
        }

        if(totalItems > MAX_ITEMS)
            throw new RuntimeException("A atividade pode ter no máximo " + MAX_ITEMS + " itens");
    }

    private String slug(String text){
        String plain = Normalizer.normalize(text.trim().toLowerCase(PT_BR), Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        return plain.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
