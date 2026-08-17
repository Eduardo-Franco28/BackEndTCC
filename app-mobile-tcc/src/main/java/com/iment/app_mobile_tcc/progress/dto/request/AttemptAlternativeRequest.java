package com.iment.app_mobile_tcc.progress.dto.request;

import java.util.List;

public record AttemptAlternativeRequest(Long questionId, List<Long> lstAlternativeId) {
}
