package com.iment.app_mobile_tcc.alternatives.service;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.alternatives.dto.response.AlternativeResponse;
import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.repository.AlternativeRepository;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.repository.QuestionRepository;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlternativeService {
    @Autowired
    private AlternativeRepository alternativeRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public AlternativeResponse create(AlternativeRequest obj) {
        if(obj.description() == null || obj.description().isBlank())
            throw new RuntimeException("A alternativa precisa de um valor");

        Question question = this.questionRepository.findById(obj.questionId()).orElseThrow(() -> new RuntimeException("Questão não encontrada"));

        try {
            Alternative alternative = new Alternative(
                    null,
                    obj.description(),
                    obj.correct(),
                    question
            );

            return AlternativeResponse.from(this.alternativeRepository.save(alternative));
        } catch (Exception e){
            throw new RuntimeException("Falha na criação da alternativa", e);
        }
    }

    public List<AlternativeResponse> getAll(){
        try {
            List<Alternative> lstAlternative = this.alternativeRepository.findAll();

            return lstAlternative.stream()
                    .map(AlternativeResponse::from)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as alternativas", e);
        }
    }

    public List<AlternativeResponse> getAllByQuestionId(Long questionId){
        try {
            List<Alternative> lstAlternative = this.alternativeRepository.findAllByQuestionId(questionId);

            return lstAlternative.stream()
                    .map(AlternativeResponse::from)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as alternativas da questão", e);
        }
    }

    public Alternative getAlternative(Long id){
        return this.alternativeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Altenrativa não encontrada"));
    }

    public List<Alternative> getAlternatives(List<Long> ids){
        List<Alternative> lstAlternative = this.alternativeRepository.findAllById(ids);

        if(lstAlternative.isEmpty())
            throw new EntityNotFoundException("Altenrativa não encontrada");

        return lstAlternative;
    }

    public Long countCorrectAlternativesByQuestion(Long questionId){
        Long alternatives = this.alternativeRepository.countByQuestionIdAndCorrectTrue(questionId);

        if(alternatives <= 0)
            throw new RuntimeException("Essa questão não possuí alternativas");

        return alternatives;
    }
}
