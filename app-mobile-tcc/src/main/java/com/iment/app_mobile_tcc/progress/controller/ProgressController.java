package com.iment.app_mobile_tcc.progress.controller;

import com.iment.app_mobile_tcc.progress.service.ProgressService;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.topics.service.TopicService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/stats")
public class ProgressController {
    @Autowired
    private ProgressService progressService;

    @Autowired
    private TopicService topicService;

    @GetMapping
    public ResponseEntity<Integer> getAllConcluedQuestions(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.progressService.getAllConcludedQuestionIds(user.getId()).size());
    }

    @GetMapping("/topic")
    public ResponseEntity<Integer> getAllTopicConclued(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.topicService.getConcluded(user).size());
    }

    @GetMapping("/{limit}/unfinished")
    public ResponseEntity<List<QuestionResponse>> getUnfinishedQuestions(@AuthenticationPrincipal User user, @PathVariable Integer limit){
        return ResponseEntity.ok(this.progressService.findUnfinishedQuestions(user, limit));
    }
}
