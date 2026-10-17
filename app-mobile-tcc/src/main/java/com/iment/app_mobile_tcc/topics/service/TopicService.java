package com.iment.app_mobile_tcc.topics.service;

import com.iment.app_mobile_tcc.progress.service.ProgressService;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.boards.service.BoardService;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.subjects.repository.SubjectRepository;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.topics.enums.TopicStatusEnum;
import com.iment.app_mobile_tcc.topics.dto.request.TopicRequest;
import com.iment.app_mobile_tcc.topics.dto.response.TopicResponse;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.repository.TopicRepository;
import com.iment.app_mobile_tcc.users.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private BoardService boardService;

    public TopicResponse create(TopicRequest obj) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        Subject subject = this.subjectRepository.findById(obj.subjectId()).orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        try {
            Topic topic = new Topic(
                    null,
                    obj.title(),
                    obj.subTitle(),
                    subject,
                    null,
                    false,
                    false,
                    null,
                    null
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

    public Topic createDraft(TopicRequest obj, User owner) {
        if(obj.title() == null || obj.title().isBlank())
            throw new RuntimeException("O nome do tópico não pode vir vazio");

        Subject subject = this.subjectRepository.findById(obj.subjectId()).orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        try {
            Topic topic = new Topic(
                    null,
                    obj.title(),
                    obj.subTitle(),
                    subject,
                    owner,
                    true,
                    false,
                    null,
                    null
            );

            return this.topicRepository.save(topic);
        } catch (Exception e){
            throw new RuntimeException("Falha na criação do rascunho", e);
        }
    }

    public Topic createCopy(Topic source, User owner, boolean catalog) {
        try {
            Topic topic = new Topic(
                    null,
                    source.getTitle(),
                    source.getSubTitle(),
                    source.getSubject(),
                    owner,
                    false,
                    catalog,
                    source,
                    null
            );

            return this.topicRepository.save(topic);
        } catch (Exception e){
            throw new RuntimeException("Falha ao copiar a atividade", e);
        }
    }

    public List<Topic> getCatalog(){
        try {
            return this.topicRepository.findAllInCatalog();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar o catálogo", e);
        }
    }

    public Topic getCatalogTopic(Long id){
        Topic topic = this.topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada no catálogo"));

        if(!topic.isCatalog() || topic.getRemovedAt() != null)
            throw new EntityNotFoundException("Atividade não encontrada no catálogo");

        return topic;
    }

    public ActivityResponse getCatalogActivity(Long id, Long userId){
        this.getCatalogTopic(id);

        return this.buildActivity(id, userId);
    }

    public List<Topic> getActiveCopies(User owner){
        try {
            return this.topicRepository.findAllByOwnerIdAndRemovedAtIsNullAndForkedFromIsNotNull(owner.getId());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as cópias da conta", e);
        }
    }

    public Map<Long, Long> countCopies(List<Long> ids){
        if(ids.isEmpty())
            return Map.of();

        return this.topicRepository.countCopies(ids).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    public void removeFromCatalog(Long sourceId, User owner){
        Topic topic = this.topicRepository
                .findFirstByOwnerIdAndForkedFromIdAndCatalogTrueAndRemovedAtIsNull(owner.getId(), sourceId)
                .orElseThrow(() -> new EntityNotFoundException("Essa atividade não está no catálogo"));

        try {
            topic.setRemovedAt(Instant.now());

            this.topicRepository.save(topic);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao tirar a atividade do catálogo", e);
        }
    }

    public List<Topic> getOwnedTopics(User owner){
        try {
            return this.topicRepository.findAllByOwnerIdAndCatalogFalseAndRemovedAtIsNull(owner.getId());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as atividades da conta", e);
        }
    }

    public Topic getOwnedTopic(Long id, User owner){
        Topic topic = this.topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        boolean isOwner = topic.getOwner() != null && topic.getOwner().getId().equals(owner.getId());

        if(!isOwner || topic.isCatalog() || topic.getRemovedAt() != null)
            throw new EntityNotFoundException("Tópico não encontrado");

        return topic;
    }

    public Topic approveDraft(Long id, User owner){
        Topic topic = this.getOwnedTopic(id, owner);

        if(!topic.isDraft())
            throw new RuntimeException("Essa atividade já foi aprovada");

        try {
            topic.setDraft(false);

            return this.topicRepository.save(topic);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao aprovar a atividade", e);
        }
    }

    public void removeFromAccount(Long id, User owner){
        Topic topic = this.getOwnedTopic(id, owner);

        try {
            topic.setRemovedAt(Instant.now());

            this.topicRepository.save(topic);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao remover a atividade", e);
        }
    }

    public ActivityResponse getOwnedActivity(Long topicId, User owner){
        this.getOwnedTopic(topicId, owner);

        return this.buildActivity(topicId, owner.getId());
    }

    public ActivityResponse getActivity(Long topicId, Long userId){
        Topic topic = this.topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        boolean isMine = topic.getOwner() == null || topic.getOwner().getId().equals(userId);

        if(!isMine || topic.isDraft() || topic.isCatalog() || topic.getRemovedAt() != null)
            throw new EntityNotFoundException("Tópico não encontrado");

        return this.buildActivity(topicId, userId);
    }

    private ActivityResponse buildActivity(Long topicId, Long userId){
        List<Question> lstQuestion = this.questionService.getAllByTopicId(topicId);

        Set<Long> concludedIds = this.progressService.getConcludedQuestionIds(userId, topicId);

        List<QuestionResponse> lstQuestionResponse = lstQuestion.stream()
                .map(question -> QuestionResponse.from(
                        question,
                        concludedIds.contains(question.getId()),
                        this.boardService.readSlots(question.getBoard())
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

    public List<TopicResponse> getBySubject(User user, Long subjectId){
        List<Topic> topics = this.topicRepository.findPlayableBySubject(subjectId, user.getId());

        if(topics.isEmpty())
            throw new RuntimeException("Nenhum tópico encontrado");

        return topics.stream()
        .map(topic -> {
              int percent = this.progressService.calcPercentConclued(user.getId(), topic.getId());
            TopicStatusEnum status = this.setTopicStatus(percent);
            return TopicResponse.from(topic, percent, status);
        })
        .toList();
    }

    public List<TopicResponse> getConcluded(User user) {
        List<TopicResponse> concluded = new ArrayList<>();

        for (Topic topic : this.topicRepository.findAllPlayable(user.getId())) {
            int percent = this.progressService.calcPercentConclued(user.getId(), topic.getId());

            if (percent == 100)
                concluded.add(TopicResponse.from(topic, percent, TopicStatusEnum.CONCLUIDO));
        }

        return concluded;
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
