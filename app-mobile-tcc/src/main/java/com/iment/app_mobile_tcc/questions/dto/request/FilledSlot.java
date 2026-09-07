package com.iment.app_mobile_tcc.questions.dto.request;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;

public record FilledSlot(Long slotId, String name, Long alternativeId) {
}
