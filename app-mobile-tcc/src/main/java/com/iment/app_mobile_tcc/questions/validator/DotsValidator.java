package com.iment.app_mobile_tcc.questions.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class DotsValidator {

    private static final int MIN_NUMBER = 1;

    private static final int MAX_TOTAL = 30;

    @Autowired
    private ObjectMapper objectMapper;

    public void validate(String content){
        JsonNode node;

        try {
            node = this.objectMapper.readTree(content);
        } catch (Exception e){
            throw new RuntimeException("O conteúdo da soma está mal formado", e);
        }

        JsonNode first = node.get("teste1");
        JsonNode second = node.get("teste2");

        if(first == null || second == null || !first.isInt() || !second.isInt())
            throw new RuntimeException("A soma precisa de dois números inteiros");

        if(first.asInt() < MIN_NUMBER || second.asInt() < MIN_NUMBER)
            throw new RuntimeException("Os números da soma precisam ser a partir de " + MIN_NUMBER);

        if(first.asInt() + second.asInt() > MAX_TOTAL)
            throw new RuntimeException("A soma pode ter no máximo " + MAX_TOTAL + " bolinhas");
    }
}
