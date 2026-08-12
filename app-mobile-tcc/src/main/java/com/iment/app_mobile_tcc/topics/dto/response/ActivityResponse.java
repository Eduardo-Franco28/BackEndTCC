package com.iment.app_mobile_tcc.topics.dto.response;

import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.topics.repository.TopicRepository;

import java.util.List;

public record ActivityResponse(Long resumeQuestionId, List<QuestionResponse> lstQuestions) {
}
