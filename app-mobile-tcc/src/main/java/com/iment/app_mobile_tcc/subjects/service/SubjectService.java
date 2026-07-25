package com.iment.app_mobile_tcc.subjects.service;

import com.iment.app_mobile_tcc.subjects.dto.request.SubjectRequest;
import com.iment.app_mobile_tcc.subjects.dto.response.SubjectResponse;
import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.subjects.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    public SubjectResponse create(SubjectRequest obj) {
        if(obj.name() == null || obj.name().isBlank())
            throw new RuntimeException("Digite um nome para a matéria");

        try {
            Subject subject = new Subject(
                    null,
                    obj.name()
            );

            return SubjectResponse.from(this.subjectRepository.save(subject));
        } catch (Exception e){
            throw new RuntimeException("Falha na criação do tópico", e);
        }
    }

    public List<SubjectResponse> getAll(){
        try {
            return this.subjectRepository.findAll().stream().map(SubjectResponse::from).toList();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao buscar as matérias", e);
        }
    }
}
