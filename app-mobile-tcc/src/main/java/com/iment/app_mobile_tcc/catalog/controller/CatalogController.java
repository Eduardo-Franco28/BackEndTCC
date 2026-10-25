package com.iment.app_mobile_tcc.catalog.controller;

import com.iment.app_mobile_tcc.catalog.dto.response.CatalogResponse;
import com.iment.app_mobile_tcc.catalog.service.CatalogService;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.topics.dto.response.ActivityResponse;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    @Autowired
    private CatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<CatalogResponse>> getAll(@AuthenticationPrincipal User user,
                                                        @RequestParam(required = false) Long subjectId,
                                                        @RequestParam(required = false) String q){
        return ResponseEntity.ok(this.catalogService.getAll(user, subjectId, q));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(this.catalogService.getActivity(id, user));
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<DraftResponse> copy(@PathVariable Long id, @AuthenticationPrincipal User user){
        DraftResponse copy = this.catalogService.copy(id, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(copy);
    }
}
