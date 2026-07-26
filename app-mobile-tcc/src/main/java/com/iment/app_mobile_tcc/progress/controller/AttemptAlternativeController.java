package com.iment.app_mobile_tcc.progress.controller;

import com.iment.app_mobile_tcc.progress.dto.request.AttemptAlternativeRequest;
import com.iment.app_mobile_tcc.progress.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.progress.service.AttemptService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class AttemptAlternativeController {
    @Autowired
    private AttemptService attemptService;

    @PostMapping("/answer")
    public ResponseEntity<AnsweredAlternativeResponse> answer(@AuthenticationPrincipal User user, @RequestBody AttemptAlternativeRequest request){
        return ResponseEntity.ok(this.attemptService.save(user, request));
    }
}
