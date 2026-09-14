package com.iment.app_mobile_tcc.boards.controller;

import com.iment.app_mobile_tcc.boards.dto.request.BoardRequest;
import com.iment.app_mobile_tcc.boards.dto.response.BoardResponse;
import com.iment.app_mobile_tcc.boards.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(@RequestBody BoardRequest request){
        return ResponseEntity.ok(this.boardService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getAll(){
        return ResponseEntity.ok(this.boardService.getAll());
    }
}
