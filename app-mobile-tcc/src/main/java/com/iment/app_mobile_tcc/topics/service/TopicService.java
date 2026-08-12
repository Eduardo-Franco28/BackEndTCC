package com.iment.app_mobile_tcc.topics.service;

import com.iment.app_mobile_tcc.progress.service.QuestionProgressService;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.subjects.repository.SubjectRepository;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.topics.enums.TopicStatusEnum;
import com.iment.app_mobile_tcc.topics.dto.request.TopicRequest;
import com.iment.app_mobile_tcc.topics.dto.response.TopicResponse;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionProgressService questionProgressService;

    @Autowired
    private SubjectRepository subjectRepository;

    public TopicResponse create(TopicRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        Subject subject = this.subjectRepository.findById(obj.subjectId()).orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        try {
            Topic topic = new Topic(
                    null,
                    obj.title(),
                    obj.subTitle(),
                    subject
            );

            return TopicResponse.from(this.topicRepository.save(topic));
        } catch (Exception e){
            throw new RuntimeException("Falha na criação do tópico", e);
        }
    }

    public TopicResponse update(Long id, TopicRequest obj){
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        Topic topic = this.topicRepository.findById(id).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        try {
            topic.setTitle(obj.title());

            Topic updatedTopic = this.topicRepository.save(topic);

            return TopicResponse.from(updatedTopic);
        } catch (Exception e) {
            throw new RuntimeException("Falha na alteração do tópico", e);
        }
    }

    public void delete(Long id){
        Topic topic = this.topicRepository.findById(id).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        this.topicRepository.delete(topic);
    }

    public ActivityResponse getActivity(Long topicId, Long userId){
        List<Question> lstQuestion = this.questionService.getAllByTopicId(topicId);

        Set<Long> concludedIds = this.questionProgressService.getConcludedQuestionIds(userId, topicId);

        List<QuestionResponse> lstQuestionResponse = lstQuestion.stream()
                .map(question -> QuestionResponse.from(
                        question,
                        concludedIds.contains(question.getId())
                ))
                .toList();

        Long resumeQuestionId = lstQuestion.stream()
                .filter(question -> !concludedIds.contains(question.getId()))
                .map(Question::getId)
                .findFirst()
                .orElse(null);


        return new ActivityResponse(
                resumeQuestionId,
                lstQuestionResponse
        );
    }

    public List<TopicResponse> getBySubject(Long subjectId){
        List<Topic> topics = this.topicRepository.findAllBySubjectId(subjectId);

        if(topics.isEmpty())
            throw new RuntimeException("Nenhum tópico encontrado");

        return topics.stream()
        .map(topic -> {
//            int percent = this.questionService.percentConclued(topic.getId());
//            TopicStatusEnum status = this.setTopicStatus(percent);
            return TopicResponse.from(topic, 0, TopicStatusEnum.NAO_INICIADO);
        })
        .toList();
    }

//    public TopicResponse get(Long id){
//            Topic topic = this.topicRepository.findById(id).orElseThrow(() -> new RuntimeException("Tópico não encontrado"));
//
//            int percent = this.questionService.percentConclued(topic.getId());
//            TopicStatusEnum status = this.setTopicStatus(percent);
//
//            return TopicResponse.from(topic, percent, status);
//    }
//
//    public List<TopicResponse> getAll(){
//        try {
//            List<Topic> topics = this.topicRepository.findAll();
//
//            return topics.stream()
//                    .map(topic -> {
//                        int percent = this.questionService.percentConclued(topic.getId());
//                        TopicStatusEnum status = this.setTopicStatus(percent);
//
//                        return TopicResponse.from(topic, percent, status);
//                    })
//                    .toList();
//        } catch (Exception e) {
//            throw new RuntimeException("Falha ao buscar pelos tópicos", e);
//        }
//    }

    private TopicStatusEnum setTopicStatus(int percent) {
        if(percent == 0)
            return TopicStatusEnum.NAO_INICIADO;

        if(percent >= 100 )
            return TopicStatusEnum.CONCLUIDO;

        return TopicStatusEnum.EM_ANDAMENTO;
    }
}
