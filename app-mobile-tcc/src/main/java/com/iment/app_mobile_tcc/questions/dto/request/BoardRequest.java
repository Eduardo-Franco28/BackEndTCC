package com.iment.app_mobile_tcc.questions.dto.request;

import com.iment.app_mobile_tcc.questions.dto.content.BoardSlot;

import java.util.List;

public record BoardRequest(
        String name,
        String viewBox,
        List<BoardSlot> slots,
        boolean allowMultiple
) {
}
