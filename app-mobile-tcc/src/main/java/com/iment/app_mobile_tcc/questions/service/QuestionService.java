package com.iment.app_mobile_tcc.questions.service;

import com.iment.app_mobile_tcc.alternatives.dto.response.AlternativeResponse;
import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Board;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.repository.BoardRepository;
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

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private BoardRepository boardRepository;

    public QuestionResponse create(QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        if(obj.content() == null)
            throw new RuntimeException("A atividade precisa de conteúdo");

        Topic topic = this.topicRepository.findById(obj.topicId()).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        Board board = null;

        if(obj.boardId() != null)
            board = this.boardRepository.findById(obj.boardId())
                    .orElseThrow(() -> new RuntimeException("Board não encontrado"));

        try {
            Question question = new Question(
                    null,
                    obj.title(),
                    topic,
                    obj.level(),
                    obj.type(),
                    obj.content().toString(),
                    List.of(),
                    board
            );

            return QuestionResponse.from(this.questionRepository.save(question), false, null);
        } catch (Exception e){
            throw new RuntimeException("Falha na criação da questão", e);
        }
    }

    public QuestionResponse update(Long id, QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome da questão não pode vir vazio");

        if(obj.content() == null)
            throw new RuntimeException("A atividade precisa de conteúdo");

        Question question = this.getQuestion(id);

        try {
            question.setTitle(obj.title());
            question.setLevel(obj.level());
            question.setType(obj.type());
            question.setContent(obj.content().toString());

            // Board nulo é legítimo: palavra e animais não têm desenho.
            question.setBoard(
                    obj.boardId() == null
                            ? null
                            : this.boardRepository.findById(obj.boardId())
                                    .orElseThrow(() -> new RuntimeException("Board não encontrado"))
            );

            Question updatedQuestion = this.questionRepository.save(question);

            return QuestionResponse.from(updatedQuestion, false, null);
        } catch (Exception e) {
            throw new RuntimeException("Falha na alteração da questão", e);
        }
    }

    public List<QuestionResponse> getAll(){
        try {
            List<Question> lstQuestion = this.questionRepository.findAll();

            return lstQuestion.stream()
                    .map(question -> QuestionResponse.from(question,false, null))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as questões", e);
        }
    }

    public QuestionResponse get(Long id){
        Question question = this.getQuestion(id);

        List<AlternativeResponse> lstAlternative = this.alternativeService.getAllByQuestionId(id);

        return QuestionResponse.from(question, false, null);
    }

    public List<Question> getAllByTopicId(Long topicId){
        try {
            return this.questionRepository.findAllByTopicIdWithAlternatives(topicId);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as questões", e);
        }
    }

    public Question getQuestion(Long id){
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Questão não encontrada"));
    }
}
