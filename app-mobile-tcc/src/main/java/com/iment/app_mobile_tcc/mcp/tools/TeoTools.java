package com.iment.app_mobile_tcc.mcp.tools;

import com.iment.app_mobile_tcc.catalog.dto.response.CatalogResponse;
import com.iment.app_mobile_tcc.catalog.service.CatalogService;
import com.iment.app_mobile_tcc.drafts.dto.request.LettersDraftRequest;
import com.iment.app_mobile_tcc.drafts.dto.request.WordRequest;
import com.iment.app_mobile_tcc.drafts.dto.response.DraftResponse;
import com.iment.app_mobile_tcc.drafts.service.DraftService;
import com.iment.app_mobile_tcc.subjects.dto.response.SubjectResponse;
import com.iment.app_mobile_tcc.subjects.service.SubjectService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeoTools {
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private DraftService draftService;

    @Autowired
    private CatalogService catalogService;

    @McpTool(name = "list_subjects", description = "Lista as matérias do IntegraMente. Use o id da matéria ao criar uma atividade.")
    public List<SubjectResponse> listSubjects(){
        return this.subjectService.getAll();
    }

    @McpTool(name = "search_catalog", description = """
            Busca atividades prontas que outros adultos publicaram no catálogo do IntegraMente.
            Use para sugerir atividades parecidas com o que o adulto pediu. Quem adiciona à conta é o adulto, pela aba Catálogo.
            """)
    public List<CatalogResponse> searchCatalog(
            @McpToolParam(description = "Palavra para buscar no título ou na matéria, por exemplo: frutas", required = false) String query){
        return this.catalogService.getAll(this.getUser(), null, query);
    }

    @McpTool(name = "list_my_drafts", description = "Lista as atividades criadas nesta conta, aprovadas ou ainda em rascunho.")
    public List<DraftResponse> listMyDrafts(){
        return this.draftService.getAll(this.getUser());
    }

    @McpTool(name = "create_letters_draft", description = """
            Cria um rascunho de atividade de completar a palavra. Não publica nada: o adulto revisa e aprova depois.
            Cada palavra tem de 2 a 8 letras, só letras. Em blanks vão as posições que ficam vazias, começando em 1:
            pelo menos uma vazia, pelo menos uma aparecendo, e as letras vazias não podem se repetir na mesma palavra.
            Em emoji vai um único emoji que representa a palavra. No máximo 10 palavras por atividade.
            """)
    public DraftResponse createLettersDraft(
            @McpToolParam(description = "Título curto da atividade, por exemplo: Animais da fazenda") String title,
            @McpToolParam(description = "Id da matéria, vindo de list_subjects. Palavras ficam em Linguagens.") Long subjectId,
            @McpToolParam(description = "As palavras da atividade, cada uma com word, blanks e emoji") List<WordRequest> words){
        return this.draftService.createLetters(new LettersDraftRequest(title, subjectId, words), this.getUser());
    }

    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !(authentication.getPrincipal() instanceof User user))
            throw new RuntimeException("Usuário não identificado");

        return user;
    }
}
