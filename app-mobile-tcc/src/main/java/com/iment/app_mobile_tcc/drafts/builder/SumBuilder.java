package com.iment.app_mobile_tcc.drafts.builder;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.SumRequest;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

@Component
public class SumBuilder {

    private static final int MIN_NUMBER = 1;

    private static final int MAX_TOTAL = 30;

    private static final int WRONG_OPTIONS = 3;

    private static final int MAX_DISTANCE = 3;

    @Autowired
    private ObjectMapper objectMapper;

    public QuestionRequest buildQuestion(SumRequest obj, int level){
        this.validate(obj);

        JsonNode content = this.objectMapper.createObjectNode()
                .put("teste1", obj.first())
                .put("teste2", obj.second());

        String title = "Quanto é " + obj.first() + " + " + obj.second() + "?";

        return new QuestionRequest(title, level, null, QuestionTypeEnum.DRAG_DOTS, content, null);
    }

    public List<AlternativeRequest> buildOptions(SumRequest obj){
        this.validate(obj);

        int total = obj.first() + obj.second();
        List<Integer> lstNear = new ArrayList<>();

        for(int distance = 1; distance <= MAX_DISTANCE; distance++){
            if(total - distance >= 0)
                lstNear.add(total - distance);

            lstNear.add(total + distance);
        }

        Collections.shuffle(lstNear);

        TreeSet<Integer> options = new TreeSet<>(lstNear.subList(0, WRONG_OPTIONS));
        options.add(total);

        return options.stream()
                .map(option -> new AlternativeRequest(String.valueOf(option), option == total, null, null, null))
                .toList();
    }

    private void validate(SumRequest obj){
        if(obj == null || obj.first() == null || obj.second() == null)
            throw new RuntimeException("Cada conta precisa de dois números");

        if(obj.first() < MIN_NUMBER || obj.second() < MIN_NUMBER)
            throw new RuntimeException("Os números da conta precisam ser a partir de " + MIN_NUMBER);

        if(obj.first() + obj.second() > MAX_TOTAL)
            throw new RuntimeException("A conta " + obj.first() + " + " + obj.second() + " passa de " + MAX_TOTAL + " bolinhas");
    }
}
