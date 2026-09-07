package com.iment.app_mobile_tcc.progress.dto.request;

import com.iment.app_mobile_tcc.questions.dto.request.FilledSlot;

import java.util.List;

public record AttemptAlternativeRequest(Long questionId, List<Long> lstAlternativeId, List<FilledSlot> lstFilledSlots) {
}
