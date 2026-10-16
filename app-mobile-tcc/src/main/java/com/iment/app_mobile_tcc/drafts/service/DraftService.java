package com.iment.app_mobile_tcc.drafts.service;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.drafts.builder.LettersBuilder;
import com.iment.app_mobile_tcc.drafts.dto.request.LettersDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.WordRequest;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
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

@Service
public class DraftService {

    private static final String DRAFT_SUBTITLE = "Criada com o Teo";

    private static final int MAX_WORDS = 10;

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private LettersBuilder lettersBuilder;

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

        return DraftResponse.from(topic);
    }

    public List<DraftResponse> getAll(User user){
        return this.topicService.getOwnedTopics(user).stream()
                .map(DraftResponse::from)
                .toList();
    }

    public ActivityResponse getActivity(Long id, User user){
        return this.topicService.getOwnedActivity(id, user);
    }

    public DraftResponse approve(Long id, User user){
        return DraftResponse.from(this.topicService.approveDraft(id, user));
    }

    public void remove(Long id, User user){
        this.topicService.removeFromAccount(id, user);
    }
}
