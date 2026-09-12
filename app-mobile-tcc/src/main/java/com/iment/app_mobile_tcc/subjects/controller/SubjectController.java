package com.iment.app_mobile_tcc.subjects.controller;

import com.iment.app_mobile_tcc.subjects.dto.request.SubjectRequest;
import com.iment.app_mobile_tcc.subjects.dto.response.SubjectResponse;
import com.iment.app_mobile_tcc.subjects.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@RequestBody SubjectRequest request){
        return ResponseEntity.ok(this.subjectService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAll(){
        return ResponseEntity.ok(this.subjectService.getAll());
    }
}
