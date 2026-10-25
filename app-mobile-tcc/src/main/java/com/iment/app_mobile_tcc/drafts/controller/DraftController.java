package com.iment.app_mobile_tcc.drafts.controller;

import com.iment.app_mobile_tcc.catalog.dto.response.CatalogResponse;
import com.iment.app_mobile_tcc.drafts.dto.request.GroupsDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.LettersDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.SumDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.drafts.service.DraftService;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drafts")
public class DraftController {
    @Autowired
    private DraftService draftService;

    @PostMapping("/letters")
    public ResponseEntity<DraftResponse> createLetters(@RequestBody LettersDraftRequest request, @AuthenticationPrincipal User user){
        DraftResponse draft = this.draftService.createLetters(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }

    @PostMapping("/sum")
    public ResponseEntity<DraftResponse> createSum(@RequestBody SumDraftRequest request, @AuthenticationPrincipal User user){
        DraftResponse draft = this.draftService.createSum(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }

    @PostMapping("/groups")
    public ResponseEntity<DraftResponse> createGroups(@RequestBody GroupsDraftRequest request, @AuthenticationPrincipal User user){
        DraftResponse draft = this.draftService.createGroups(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(draft);
    }

    @GetMapping
    public ResponseEntity<List<DraftResponse>> getAll(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.draftService.getAll(user));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.draftService.getActivity(id, user));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<DraftResponse> approve(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.draftService.approve(id, user));
    }

    @PostMapping("/{id}/remove")
    public ResponseEntity<Void> remove(@PathVariable Long id, @AuthenticationPrincipal User user){
        this.draftService.remove(id, user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CatalogResponse> publish(@PathVariable Long id, @AuthenticationPrincipal User user){
        CatalogResponse published = this.draftService.publish(id, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(published);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublish(@PathVariable Long id, @AuthenticationPrincipal User user){
        this.draftService.unpublish(id, user);

        return ResponseEntity.noContent().build();
    }
}
