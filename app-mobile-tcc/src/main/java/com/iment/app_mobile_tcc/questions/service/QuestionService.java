package com.iment.app_mobile_tcc.questions.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.repository.QuestionRepository;
import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.topics.dto.request.TopicRequest;
import com.iment.app_mobile_tcc.topics.dto.response.TopicResponse;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionResponse create(QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        Topic topic = this.topicRepository.findById(obj.topicId()).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        try {
            Question question = new Question(
                    null,
                    obj.title(),
                    topic
            );

            return QuestionResponse.from(this.questionRepository.save(question));
        } catch (Exception e){
            throw new RuntimeException("Falha na criação do tópico", e);
        }
    }

    public List<QuestionResponse> getAll(){
        try {
            List<Question> questions = this.questionRepository.findAll();

            return questions.stream()
                    .map(QuestionResponse::from)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as questões", e);
        }
    }

    public Question getQuestion(Long id){
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Questão não encontrada"));
    }
}
