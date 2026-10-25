package com.iment.app_mobile_tcc.drafts.dto.request;

import java.util.List;

public record GroupsDraftRequest(String title, Long subjectId, String question, List<GroupRequest> groups) {
}
