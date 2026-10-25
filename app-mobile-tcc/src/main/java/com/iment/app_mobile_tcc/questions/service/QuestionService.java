package com.iment.app_mobile_tcc.questions.service;

import com.iment.app_mobile_tcc.alternatives.dto.response.AlternativeResponse;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.boards.entity.Board;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import com.iment.app_mobile_tcc.questions.validator.DotsValidator;
import com.iment.app_mobile_tcc.questions.validator.GroupsValidator;
import com.iment.app_mobile_tcc.questions.validator.LettersValidator;
import com.iment.app_mobile_tcc.boards.repository.BoardRepository;
import com.iment.app_mobile_tcc.questions.repository.QuestionRepository;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private LettersValidator lettersValidator;

    @Autowired
    private DotsValidator dotsValidator;

    @Autowired
    private GroupsValidator groupsValidator;

    public QuestionResponse create(QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        if(obj.content() == null)
            throw new RuntimeException("A atividade precisa de conteúdo");

        Topic topic = this.topicRepository.findById(obj.topicId()).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        return QuestionResponse.from(this.createQuestion(topic, obj), false, null);
    }

    public Question createQuestion(Topic topic, QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome da questão não pode vir vazio");

        if(obj.content() == null)
            throw new RuntimeException("A atividade precisa de conteúdo");

        this.validateContent(obj.type(), obj.content().toString());

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

            return this.questionRepository.save(question);
        } catch (Exception e){
            throw new RuntimeException("Falha na criação da questão", e);
        }
    }

    public Question copyQuestion(Question source, Topic target) {
        try {
            Question question = new Question(
                    null,
                    source.getTitle(),
                    target,
                    source.getLevel(),
                    source.getType(),
                    source.getContent(),
                    List.of(),
                    source.getBoard()
            );

            return this.questionRepository.save(question);
        } catch (Exception e){
            throw new RuntimeException("Falha ao copiar a questão", e);
        }
    }

    public QuestionResponse update(Long id, QuestionRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome da questão não pode vir vazio");

        if(obj.content() == null)
            throw new RuntimeException("A atividade precisa de conteúdo");

        this.validateContent(obj.type(), obj.content().toString());

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

    public long countAllByTopicId(Long topicId){
        try {
            return this.questionRepository.countAllByTopicId(topicId);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao contar as questões", e);
        }
    }

    public Question getQuestion(Long id){
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Questão não encontrada"));
    }

    public Map<Long, QuestionTypeEnum> getTypes(List<Long> topicIds){
        if(topicIds.isEmpty())
            return Map.of();

        try {
            Map<Long, QuestionTypeEnum> types = new HashMap<>();

            for(Object[] row : this.questionRepository.findTypesByTopicIds(topicIds))
                types.putIfAbsent((Long) row[0], (QuestionTypeEnum) row[1]);

            return types;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar o tipo das atividades", e);
        }
    }

    public QuestionTypeEnum getType(Long topicId){
        return this.getTypes(List.of(topicId)).get(topicId);
    }

    private void validateContent(QuestionTypeEnum type, String content){
        if(type == QuestionTypeEnum.DRAG_LETTERS)
            this.lettersValidator.validate(content);

        if(type == QuestionTypeEnum.DRAG_DOTS)
            this.dotsValidator.validate(content);

        if(type == QuestionTypeEnum.DRAG_SLOTS_TO_GROUP)
            this.groupsValidator.validate(content);
    }
}
