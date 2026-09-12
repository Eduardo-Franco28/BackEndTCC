package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.progress.dto.correction.CorrectionResult;
import com.iment.app_mobile_tcc.progress.dto.request.AttemptAlternativeRequest;
import com.iment.app_mobile_tcc.progress.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import com.iment.app_mobile_tcc.progress.repository.QuestionAttemptRepository;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AttemptService {

    @Autowired
    private QuestionAttemptRepository questionAttemptRepository;

    @Autowired
    private QuestionProgressService questionProgressService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CorrectionService correctionService;

    public AnsweredAlternativeResponse save(User user, AttemptAlternativeRequest obj) {
        Question question = this.questionService.getQuestion(obj.questionId());

        CorrectionResult result;

        // As regras do jogo moram no CorrectionService. Aqui é só o fluxo.
        if (question.getType() == QuestionTypeEnum.DRAG_TO_SLOTS)
            result = this.correctionService.bySlots(question, obj);
        else
            result = this.correctionService.byAlternatives(question, obj);

        // Grava acertando ou errando. É esse histórico que alimenta o
        // percentual do tópico e o resumeQuestionId.
        this.questionAttemptRepository.save(new QuestionAttempt(
                null,
                user,
                question,
                result.correct(),
                Instant.now()
        ));

        // Concluída se acertou agora, ou se já tinha acertado antes.
        boolean questionConcluded = result.correct()
                || this.questionProgressService.isCompleted(user.getId(), obj.questionId());

        return new AnsweredAlternativeResponse(
                result.correct(),
                questionConcluded,
                result.lstWrongSlots()
        );
    }
}
