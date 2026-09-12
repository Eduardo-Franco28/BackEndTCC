package com.iment.app_mobile_tcc.questions.controller;

import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> create(@RequestBody QuestionRequest request){
        return ResponseEntity.ok(this.questionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> update(@PathVariable Long id, @RequestBody QuestionRequest request){
        return ResponseEntity.ok(this.questionService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAll(){
        return ResponseEntity.ok(this.questionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(this.questionService.get(id));
    }
}
