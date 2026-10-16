package com.iment.app_mobile_tcc.drafts.dto.request;

import java.util.List;

public record LettersDraftRequest(String title, Long subjectId, List<WordRequest> words) {
}
