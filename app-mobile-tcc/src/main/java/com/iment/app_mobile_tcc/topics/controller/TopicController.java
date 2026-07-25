package com.iment.app_mobile_tcc.topics.controller;

import com.iment.app_mobile_tcc.topics.dto.request.TopicRequest;
import com.iment.app_mobile_tcc.topics.dto.response.TopicResponse;
import com.iment.app_mobile_tcc.topics.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponse> create(@RequestBody TopicRequest request){
        TopicResponse topic = this.topicService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> update(@PathVariable Long id, @RequestBody TopicRequest request){
        TopicResponse topic = this.topicService.update(id, request);

        return ResponseEntity.ok(topic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.topicService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<List<TopicResponse>> getBySubject(@PathVariable Long subjectId){
        List<TopicResponse> topics = this.topicService.getBySubject(subjectId);

        return ResponseEntity.ok(topics);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<TopicResponse> get(@PathVariable Long id){
//        TopicResponse topic = this.topicService.get(id);
//
//        return ResponseEntity.ok(topic);
//    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAll(){
        List<TopicResponse> topics = this.topicService.getAll();

        return ResponseEntity.ok(topics);
    }
}
