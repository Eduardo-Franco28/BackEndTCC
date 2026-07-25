package com.iment.app_mobile_tcc.progress.controller;

import com.iment.app_mobile_tcc.progress.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.progress.service.AttemptAlternativeService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
public class AttemptAlternativeController {
    @Autowired
    private AttemptAlternativeService attemptAlternativeService;

    @PostMapping("/{alternativeId}")
    public ResponseEntity<AnsweredAlternativeResponse> answer(@AuthenticationPrincipal User user, @PathVariable Long alternativeId){
        return ResponseEntity.ok(this.attemptAlternativeService.save(user, alternativeId));
    }
}
