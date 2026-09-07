package com.iment.app_mobile_tcc.questions.dto.request;

import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import tools.jackson.databind.JsonNode;

public record QuestionRequest(String title, Integer level, Long topicId, QuestionTypeEnum type, JsonNode content, Long boardId) {
}
