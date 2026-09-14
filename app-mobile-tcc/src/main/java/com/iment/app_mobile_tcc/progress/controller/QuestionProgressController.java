package com.iment.app_mobile_tcc.progress.controller;

import com.iment.app_mobile_tcc.progress.service.QuestionProgressService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/stats")
public class QuestionProgressController {
    @Autowired
    private QuestionProgressService questionProgressService;

    @GetMapping
    public ResponseEntity<Set<Long>> getAllConcluedQuestions(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.questionProgressService.getAllConcludedQuestionIds(user.getId()));
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<Set<Long>> getAllTopicConclued
}
