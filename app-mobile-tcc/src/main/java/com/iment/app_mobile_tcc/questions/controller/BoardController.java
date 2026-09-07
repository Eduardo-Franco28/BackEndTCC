package com.iment.app_mobile_tcc.questions.controller;

import com.iment.app_mobile_tcc.questions.dto.request.BoardRequest;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.BoardResponse;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardRequest request){
        return ResponseEntity.ok(this.boardService.create(request));
    }
}
