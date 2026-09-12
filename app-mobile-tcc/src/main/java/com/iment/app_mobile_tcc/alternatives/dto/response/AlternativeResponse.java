package com.iment.app_mobile_tcc.alternatives.dto.response;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.questions.dto.content.BoardSlot;

import java.util.List;

/**
 * Uma peca, do jeito que o app precisa dela.
 *
 * O `path` e o `bbox` NAO sao guardados na alternativa: sao copiados do slot do
 * board cujo nome bate com o correctSlot. Funciona quando a peca tem o formato
 * do buraco (mapa, corpo). Quando nao tem desenho, os dois vem nulos e quem
 * aparece na tela e o `icon`.
 */
public record AlternativeResponse(Long id, String description, String path, String bbox, String icon) {
    public static AlternativeResponse from(Alternative alternative){
        return new AlternativeResponse(
                alternative.getId(),
                alternative.getDescription(),
                null,
                null,
                alternative.getIcon()
        );
    }

    public static AlternativeResponse from(Alternative alternative,
                                           List<BoardSlot> boardSlots) {
        // Sem os slots do board nao da pra descobrir o desenho da peca.
        // Acontece em getAll/get, que nao carregam o board.
        if (boardSlots == null) return from(alternative);

        BoardSlot slot = boardSlots.stream()
                .filter(s -> s.name().equals(alternative.getCorrectSlot()))
                .findFirst()
                .orElse(null);

        if (slot == null) return from(alternative);

        return new AlternativeResponse(
                alternative.getId(), alternative.getDescription(),
                slot.path(), slot.bbox(), alternative.getIcon());
    }
}
