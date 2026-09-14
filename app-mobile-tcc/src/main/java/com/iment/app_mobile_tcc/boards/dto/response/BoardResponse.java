package com.iment.app_mobile_tcc.boards.dto.response;

import com.iment.app_mobile_tcc.boards.entity.Board;

public record BoardResponse (Long id, String viewBox, String slots){
    public static BoardResponse from(Board board){
        return new BoardResponse(
                board.getId(),
                board.getViewBox(),
                board.getSlots()
        );
    }
}
