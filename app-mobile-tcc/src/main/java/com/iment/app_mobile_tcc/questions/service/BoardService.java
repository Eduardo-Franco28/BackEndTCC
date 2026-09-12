package com.iment.app_mobile_tcc.questions.service;

import com.iment.app_mobile_tcc.questions.dto.content.BoardSlot;
import com.iment.app_mobile_tcc.questions.dto.request.BoardRequest;
import com.iment.app_mobile_tcc.questions.dto.response.BoardResponse;
import com.iment.app_mobile_tcc.questions.entity.Board;
import com.iment.app_mobile_tcc.questions.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public BoardResponse create(BoardRequest request){
        this.validate(request);

        try {
            Board board = new Board(
                    null,
                    request.name(),
                    request.viewBox(),
                    this.objectMapper.writeValueAsString(request.slots()),
                    request.allowMultiple()
            );

            return BoardResponse.from(this.boardRepository.save(board));
        } catch (Exception e){
            throw new RuntimeException("Falha na criação do board", e);
        }
    }

    /**
     * Lista os tabuleiros cadastrados.
     *
     * Serve pra cadastrar uma questao: sem isso nao ha como descobrir o id de
     * um board pra passar no boardId da QuestionRequest.
     */
    public List<BoardResponse> getAll(){
        return this.boardRepository.findAll().stream()
                .map(BoardResponse::from)
                .toList();
    }

    public List<BoardSlot> readSlots(Board board){

        if (board == null) return List.of();

        try {
            return List.of(this.objectMapper.readValue(board.getSlots(), BoardSlot[].class));
        } catch (RuntimeException e) {
            throw new RuntimeException("Falha na leitura dos boards: ", e);
        }
    }

    private void validate(BoardRequest obj) {

        // Sem os 4 números da moldura, o app não sabe em que tamanho desenhar.
        if (obj.viewBox() == null || obj.viewBox().trim().split("\\s+").length != 4)
            throw new RuntimeException("A moldura precisa ter 4 números");

        if (obj.slots() == null || obj.slots().isEmpty())
            throw new RuntimeException("O tabuleiro precisa de pelo menos um lugar");

        var vistos = new HashSet<String>();

        for (BoardSlot slot : obj.slots()) {

            if (slot.name() == null || slot.name().isBlank())
                throw new RuntimeException("Lugar sem nome");

            // add devolve false se o item já estava no conjunto.
            // Dois lugares com o mesmo nome deixariam o gabarito ambíguo.
            if (!vistos.add(slot.name()))
                throw new RuntimeException("Lugar repetido: " + slot.name());

            // Todo traço de SVG começa com M. Pega desenho vazio ou lixo.
            if (slot.path() == null || !slot.path().trim().startsWith("M"))
                throw new RuntimeException("Desenho inválido no lugar " + slot.name());
        }
    }
}
