package com.iment.app_mobile_tcc.alternatives.controller;

import com.iment.app_mobile_tcc.alternatives.dto.request.AlternativeRequest;
import com.iment.app_mobile_tcc.alternatives.dto.response.AlternativeResponse;
import com.iment.app_mobile_tcc.alternatives.repository.AlternativeRepository;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.questions.dto.request.QuestionRequest;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("alternatives")
public class AlternativeController {
    @Autowired
    private AlternativeService alternativeService;

    @PostMapping
    public ResponseEntity<AlternativeResponse> create(@RequestBody AlternativeRequest request){
        return ResponseEntity.ok(this.alternativeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlternativeResponse> update(@PathVariable Long id, @RequestBody AlternativeRequest request){
        return ResponseEntity.ok(this.alternativeService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<AlternativeResponse>> getAll(){
        return ResponseEntity.ok(this.alternativeService.getAll());
    }
}
