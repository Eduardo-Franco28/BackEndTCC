package com.iment.app_mobile_tcc.questions.validator;

import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlot;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlotContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

@Component
public class LettersValidator {

    private static final int MIN_LETTERS = 2;

    private static final int MAX_LETTERS = 8;

    @Autowired
    private ObjectMapper objectMapper;

    public void validate(String content){
        QuestionSlotContent slotContent;

        try {
            slotContent = this.objectMapper.readValue(content, QuestionSlotContent.class);
        } catch (Exception e){
            throw new RuntimeException("O conteúdo da palavra está mal formado", e);
        }

        if(slotContent.hint() == null || slotContent.hint().isBlank())
            throw new RuntimeException("A palavra precisa de um emoji de dica");

        if(slotContent.slots() == null || slotContent.slots().size() < MIN_LETTERS || slotContent.slots().size() > MAX_LETTERS)
            throw new RuntimeException("A palavra precisa ter de " + MIN_LETTERS + " a " + MAX_LETTERS + " letras");

        Set<String> names = new HashSet<>();
        int blanks = 0;

        for(QuestionSlot slot : slotContent.slots()){
            if(slot.name() == null || slot.name().isBlank())
                throw new RuntimeException("Letra sem posição");

            if(!names.add(slot.name()))
                throw new RuntimeException("Posição repetida: " + slot.name());

            if(slot.blank()){
                blanks++;

                if(slot.label() != null)
                    throw new RuntimeException("A posição " + slot.name() + " está vazia mas mostra a letra");
            } else if(slot.label() == null || slot.label().isBlank()){
                throw new RuntimeException("A posição " + slot.name() + " precisa mostrar a letra");
            }
        }

        if(blanks == 0)
            throw new RuntimeException("A palavra precisa de pelo menos uma letra para completar");

        if(blanks == slotContent.slots().size())
            throw new RuntimeException("A palavra precisa de pelo menos uma letra aparecendo");
    }
}
