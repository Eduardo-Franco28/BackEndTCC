package com.iment.app_mobile_tcc.questions.dto.response;

import com.iment.app_mobile_tcc.questions.entity.Board;

public record BoardResponse (String viewBox, String slots){
    public static BoardResponse from(Board board){
        return new BoardResponse(
                board.getViewBox(),
                board.getSlots()
        );
    }
}
