package com.iment.app_mobile_tcc.drafts.service;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.catalog.dto.response.CatalogResponse;
import com.iment.app_mobile_tcc.catalog.service.CatalogService;
import com.iment.app_mobile_tcc.drafts.builder.GroupsBuilder;
import com.iment.app_mobile_tcc.drafts.builder.LettersBuilder;
import com.iment.app_mobile_tcc.drafts.builder.SumBuilder;
import com.iment.app_mobile_tcc.drafts.dto.request.GroupsDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.LettersDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.SumDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.SumRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.WordRequest;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.topics.dto.request.TopicRequest;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import com.iment.app_mobile_tcc.topics.service.TopicService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DraftService {

    private static final String DRAFT_SUBTITLE = "Criada com o Teo";

    private static final int MAX_WORDS = 10;

    private static final int MAX_SUMS = 10;

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private LettersBuilder lettersBuilder;

    @Autowired
    private SumBuilder sumBuilder;

    @Autowired
    private GroupsBuilder groupsBuilder;

    @Autowired
    private CatalogService catalogService;

    @Transactional
    public DraftResponse createLetters(LettersDraftRequest obj, User user){
        if(obj.words() == null || obj.words().isEmpty())
            throw new RuntimeException("A atividade precisa de pelo menos uma palavra");

        if(obj.words().size() > MAX_WORDS)
            throw new RuntimeException("A atividade pode ter no máximo " + MAX_WORDS + " palavras");

        Topic topic = this.topicService.createDraft(new TopicRequest(obj.title(), DRAFT_SUBTITLE, obj.subjectId()), user);

        for(int i = 0; i < obj.words().size(); i++){
            WordRequest word = obj.words().get(i);

            Question question = this.questionService.createQuestion(topic, this.lettersBuilder.buildQuestion(word, i + 1));

            for(AlternativeRequest piece : this.lettersBuilder.buildPieces(word))
                this.alternativeService.createAlternative(question, piece);
        }

        return DraftResponse.from(topic, false, QuestionTypeEnum.DRAG_LETTERS);
    }

    @Transactional
    public DraftResponse createSum(SumDraftRequest obj, User user){
        if(obj.sums() == null || obj.sums().isEmpty())
            throw new RuntimeException("A atividade precisa de pelo menos uma conta");

        if(obj.sums().size() > MAX_SUMS)
            throw new RuntimeException("A atividade pode ter no máximo " + MAX_SUMS + " contas");

        Topic topic = this.topicService.createDraft(new TopicRequest(obj.title(), DRAFT_SUBTITLE, obj.subjectId()), user);

        for(int i = 0; i < obj.sums().size(); i++){
            SumRequest sum = obj.sums().get(i);

            Question question = this.questionService.createQuestion(topic, this.sumBuilder.buildQuestion(sum, i + 1));

            for(AlternativeRequest option : this.sumBuilder.buildOptions(sum))
                this.alternativeService.createAlternative(question, option);
        }

        return DraftResponse.from(topic, false, QuestionTypeEnum.DRAG_DOTS);
    }

    @Transactional
    public DraftResponse createGroups(GroupsDraftRequest obj, User user){
        Topic topic = this.topicService.createDraft(new TopicRequest(obj.title(), DRAFT_SUBTITLE, obj.subjectId()), user);

        Question question = this.questionService.createQuestion(topic, this.groupsBuilder.buildQuestion(obj));

        for(AlternativeRequest piece : this.groupsBuilder.buildPieces(obj))
            this.alternativeService.createAlternative(question, piece);

        return DraftResponse.from(topic, false, QuestionTypeEnum.DRAG_SLOTS_TO_GROUP);
    }

    public List<DraftResponse> getAll(User user){
        Set<Long> publishedIds = this.catalogService.getPublishedIds(user);
        List<Topic> lstTopic = this.topicService.getOwnedTopics(user);
        Map<Long, QuestionTypeEnum> types = this.questionService.getTypes(lstTopic.stream().map(Topic::getId).toList());

        return lstTopic.stream()
                .map(topic -> DraftResponse.from(topic, publishedIds.contains(topic.getId()), types.get(topic.getId())))
                .toList();
    }

    public ActivityResponse getActivity(Long id, User user){
        return this.topicService.getOwnedActivity(id, user);
    }

    public DraftResponse approve(Long id, User user){
        Topic topic = this.topicService.approveDraft(id, user);

        return DraftResponse.from(topic, false, this.questionService.getType(topic.getId()));
    }

    @Transactional
    public void remove(Long id, User user){
        this.topicService.removeFromAccount(id, user);
        this.catalogService.unpublishIfPublished(id, user);
    }

    public CatalogResponse publish(Long id, User user){
        return this.catalogService.publish(id, user);
    }

    public void unpublish(Long id, User user){
        this.catalogService.unpublish(id, user);
    }
}
