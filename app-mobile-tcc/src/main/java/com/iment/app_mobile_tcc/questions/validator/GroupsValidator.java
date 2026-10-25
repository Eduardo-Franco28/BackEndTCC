package com.iment.app_mobile_tcc.questions.validator;

import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlot;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlotContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

@Component
public class GroupsValidator {

    private static final int MIN_GROUPS = 2;

    private static final int MAX_GROUPS = 3;

    @Autowired
    private ObjectMapper objectMapper;

    public void validate(String content){
        QuestionSlotContent slotContent;

        try {
            slotContent = this.objectMapper.readValue(content, QuestionSlotContent.class);
        } catch (Exception e){
            throw new RuntimeException("O conteúdo dos grupos está mal formado", e);
        }

        if(slotContent.slots() == null || slotContent.slots().size() < MIN_GROUPS || slotContent.slots().size() > MAX_GROUPS)
            throw new RuntimeException("A atividade precisa ter de " + MIN_GROUPS + " a " + MAX_GROUPS + " grupos");

        Set<String> names = new HashSet<>();

        for(QuestionSlot slot : slotContent.slots()){
            if(slot.name() == null || slot.name().isBlank() || !names.add(slot.name()))
                throw new RuntimeException("Cada grupo precisa de um nome único");

            if(!slot.blank())
                throw new RuntimeException("O grupo " + slot.name() + " precisa receber itens");

            if(slot.label() == null || slot.label().isBlank())
                throw new RuntimeException("O grupo " + slot.name() + " precisa de um nome para aparecer na tela");
        }
    }
}
