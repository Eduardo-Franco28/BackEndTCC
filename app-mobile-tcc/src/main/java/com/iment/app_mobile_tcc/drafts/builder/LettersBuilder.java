package com.iment.app_mobile_tcc.drafts.builder;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.WordRequest;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlot;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlotContent;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Component
public class LettersBuilder {

    private static final String QUESTION_TITLE = "Complete a palavra";

    private static final int MIN_LETTERS = 2;

    private static final int MAX_LETTERS = 8;

    private static final int DISTRACTORS = 2;

    private static final String DISTRACTOR_POOL = "AEIOUBCDFGLMNPRST";

    private static final Pattern ONLY_LETTERS = Pattern.compile("[A-ZÁÀÂÃÉÊÍÓÔÕÚÇ]+");

    @Autowired
    private ObjectMapper objectMapper;

    public QuestionRequest buildQuestion(WordRequest obj, int level){
        String word = this.normalize(obj.word());
        Set<Integer> blanks = this.validate(word, obj);

        List<QuestionSlot> lstSlot = new ArrayList<>();

        for(int i = 0; i < word.length(); i++){
            int position = i + 1;
            String name = String.valueOf(position);

            if(blanks.contains(position))
                lstSlot.add(new QuestionSlot(name, true, null));
            else
                lstSlot.add(new QuestionSlot(name, false, String.valueOf(word.charAt(i))));
        }

        JsonNode content = this.objectMapper.valueToTree(new QuestionSlotContent(lstSlot, obj.emoji().trim()));

        return new QuestionRequest(QUESTION_TITLE, level, null, QuestionTypeEnum.DRAG_LETTERS, content, null);
    }

    public List<AlternativeRequest> buildPieces(WordRequest obj){
        String word = this.normalize(obj.word());
        Set<Integer> blanks = this.validate(word, obj);

        List<AlternativeRequest> pieces = new ArrayList<>();

        for(Integer position : blanks)
            pieces.add(new AlternativeRequest(String.valueOf(word.charAt(position - 1)), true, null, String.valueOf(position), null));

        DISTRACTOR_POOL.chars()
                .mapToObj(letter -> String.valueOf((char) letter))
                .filter(letter -> !word.contains(letter))
                .limit(DISTRACTORS)
                .forEach(letter -> pieces.add(new AlternativeRequest(letter, false, null, null, null)));

        return pieces;
    }

    private String normalize(String word){
        if(word == null || word.isBlank())
            throw new RuntimeException("Informe a palavra");

        return word.trim().toUpperCase(Locale.forLanguageTag("pt-BR"));
    }

    private Set<Integer> validate(String word, WordRequest obj){
        if(!ONLY_LETTERS.matcher(word).matches())
            throw new RuntimeException("A palavra " + word + " só pode ter letras");

        if(word.length() < MIN_LETTERS || word.length() > MAX_LETTERS)
            throw new RuntimeException("A palavra " + word + " precisa ter de " + MIN_LETTERS + " a " + MAX_LETTERS + " letras");

        if(obj.emoji() == null || obj.emoji().isBlank())
            throw new RuntimeException("A palavra " + word + " precisa de um emoji de dica");

        if(obj.blanks() == null || obj.blanks().isEmpty())
            throw new RuntimeException("Escolha pelo menos uma letra para completar em " + word);

        Set<Integer> blanks = new TreeSet<>(obj.blanks());

        if(blanks.size() != obj.blanks().size())
            throw new RuntimeException("Posição vazia repetida em " + word);

        if(blanks.size() >= word.length())
            throw new RuntimeException("Deixe pelo menos uma letra aparecendo em " + word);

        Set<Character> blankLetters = new HashSet<>();

        for(Integer position : blanks){
            if(position == null || position < 1 || position > word.length())
                throw new RuntimeException("A posição " + position + " não existe em " + word);

            char letter = word.charAt(position - 1);

            if(!blankLetters.add(letter))
                throw new RuntimeException("As letras que ficam vazias não podem se repetir: " + letter + " em " + word);
        }

        return blanks;
    }
}
