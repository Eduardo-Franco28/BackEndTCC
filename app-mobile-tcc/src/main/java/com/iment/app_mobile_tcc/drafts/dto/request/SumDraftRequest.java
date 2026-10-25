package com.iment.app_mobile_tcc.drafts.dto.request;

import java.util.List;

public record SumDraftRequest(String title, Long subjectId, List<SumRequest> sums) {
}
