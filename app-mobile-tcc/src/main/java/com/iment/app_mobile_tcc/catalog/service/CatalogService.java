package com.iment.app_mobile_tcc.catalog.service;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.catalog.dto.response.CatalogResponse;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.service.TopicService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    @Autowired
    private TopicService topicService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AlternativeService alternativeService;

    @Transactional
    public CatalogResponse publish(Long id, User user){
        Topic source = this.topicService.getOwnedTopic(id, user);

        if(source.isDraft())
            throw new RuntimeException("Aprove a atividade antes de publicar no catálogo");

        if(source.getForkedFrom() != null)
            throw new RuntimeException("Só dá para publicar atividades criadas por você");

        if(this.getPublishedIds(user).contains(id))
            throw new RuntimeException("Essa atividade já está no catálogo");

        Topic published = this.copyTopic(source, user, true);

        return CatalogResponse.from(published, 0, true, false);
    }

    @Transactional
    public void unpublish(Long id, User user){
        this.topicService.getOwnedTopic(id, user);
        this.topicService.removeFromCatalog(id, user);
    }

    public void unpublishIfPublished(Long id, User user){
        if(this.getPublishedIds(user).contains(id))
            this.topicService.removeFromCatalog(id, user);
    }

    public List<CatalogResponse> getAll(User user, Long subjectId, String query){
        String search = query == null ? "" : query.trim().toLowerCase(Locale.forLanguageTag("pt-BR"));

        List<Topic> lstTopic = this.topicService.getCatalog().stream()
                .filter(topic -> subjectId == null || topic.getSubject().getId().equals(subjectId))
                .filter(topic -> search.isEmpty()
                        || topic.getTitle().toLowerCase(Locale.forLanguageTag("pt-BR")).contains(search)
                        || topic.getSubject().getName().toLowerCase(Locale.forLanguageTag("pt-BR")).contains(search))
                .toList();

        Map<Long, Long> copies = this.topicService.countCopies(lstTopic.stream().map(Topic::getId).toList());
        Set<Long> addedIds = this.getAddedIds(user);

        return lstTopic.stream()
                .map(topic -> CatalogResponse.from(
                        topic,
                        copies.getOrDefault(topic.getId(), 0L),
                        topic.getOwner().getId().equals(user.getId()),
                        addedIds.contains(topic.getId())
                ))
                .toList();
    }

    public ActivityResponse getActivity(Long id, User user){
        return this.topicService.getCatalogActivity(id, user.getId());
    }

    @Transactional
    public DraftResponse copy(Long id, User user){
        Topic source = this.topicService.getCatalogTopic(id);

        if(source.getOwner().getId().equals(user.getId()))
            throw new RuntimeException("Essa atividade é sua, ela já está na sua conta");

        if(this.getAddedIds(user).contains(id))
            throw new RuntimeException("Você já tem essa atividade na conta");

        return DraftResponse.from(this.copyTopic(source, user, false), false);
    }

    public Set<Long> getPublishedIds(User user){
        return this.topicService.getActiveCopies(user).stream()
                .filter(Topic::isCatalog)
                .map(topic -> topic.getForkedFrom().getId())
                .collect(Collectors.toSet());
    }

    private Set<Long> getAddedIds(User user){
        return this.topicService.getActiveCopies(user).stream()
                .filter(topic -> !topic.isCatalog())
                .map(topic -> topic.getForkedFrom().getId())
                .collect(Collectors.toSet());
    }

    private Topic copyTopic(Topic source, User owner, boolean catalog){
        Topic copy = this.topicService.createCopy(source, owner, catalog);

        for(Question question : this.questionService.getAllByTopicId(source.getId())){
            Question questionCopy = this.questionService.copyQuestion(question, copy);

            for(Alternative alternative : question.getLstAlternative())
                this.alternativeService.createAlternative(questionCopy, new AlternativeRequest(
                        alternative.getDescription(),
                        alternative.isCorrect(),
                        null,
                        alternative.getCorrectSlot(),
                        alternative.getIcon()
                ));
        }

        return copy;
    }
}
