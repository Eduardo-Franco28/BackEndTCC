package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.progress.dto.correction.CorrectionResult;
import com.iment.app_mobile_tcc.progress.dto.request.AttemptAlternativeRequest;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionContent;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlot;
import com.iment.app_mobile_tcc.questions.dto.request.FilledSlot;
import com.iment.app_mobile_tcc.questions.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * As regras do jogo. Diz se a criança acertou — e mais nada.
 * Não busca no banco, não grava e não sabe o que é HTTP.
 */
@Service
public class CorrectionService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlternativeService alternativeService;

    // ==================================================================
    // Atividades de arrastar: palavra, mapa, corpo e animais
    // ==================================================================

    public CorrectionResult bySlots(Question question, AttemptAlternativeRequest obj) {

        List<FilledSlot> placed = obj.lstFilledSlots();

        Set<String> emptySlots = this.getEmptySlots(question);
        Map<Long, Alternative> pieces = this.getPiecesById(question);

        this.validateSlots(placed, emptySlots, pieces, question);

        return this.checkSlots(placed, pieces);
    }

    /** Os lugares que estavam vazios e a criança precisava preencher. */
    private Set<String> getEmptySlots(Question question) {
        return this.readContent(question).slots().stream()
                .filter(QuestionSlot::blank)
                .map(QuestionSlot::name)
                .collect(Collectors.toSet());
    }

    /** As peças desta questão, indexadas pelo id, pra consulta direta. */
    private Map<Long, Alternative> getPiecesById(Question question) {
        return question.getLstAlternative().stream()
                .collect(Collectors.toMap(Alternative::getId, piece -> piece));
    }

    /**
     * Tudo aqui é ERRO DE REQUISIÇÃO, não resposta errada.
     * "Errou" faz parte do jogo; o que está abaixo é bug do app ou
     * tentativa de burlar.
     */
    private void validateSlots(List<FilledSlot> placed, Set<String> emptySlots,
                               Map<Long, Alternative> pieces, Question question) {

        if (placed == null || placed.isEmpty())
            throw new RuntimeException("Nenhuma peça foi colocada");

        if (placed.size() != emptySlots.size())
            throw new RuntimeException("Faltam peças para completar");

        for (FilledSlot slot : placed) {
            Alternative piece = pieces.get(slot.alternativeId());

            // Peça de outra questão. Sem isto dá pra confundir a correção.
            if (piece == null)
                throw new RuntimeException("Essa peça não pertence a esta questão");

            // Peça cadastrada sem gabarito: erro de quem criou a questão.
            if (piece.getCorrectSlot() == null)
                throw new RuntimeException("Peça sem gabarito: " + piece.getDescription());

            // Lugar que não existe, ou que já vinha preenchido (o tronco).
            if (!emptySlots.contains(slot.name()))
                throw new RuntimeException("Lugar inválido ou já preenchido: " + slot.name());
        }

        this.validateOnePiecePerSlot(placed, question);
    }

    /**
     * Duas peças no mesmo lugar.
     * Proibido no corpo e no mapa; permitido nos animais, onde o mar recebe
     * golfinho e peixe — é exatamente pra isso que o allowMultiple existe.
     */
    private void validateOnePiecePerSlot(List<FilledSlot> placed, Question question) {

        if (question.getBoard() != null && question.getBoard().isAllowMultiple())
            return;

        long distinctSlots = placed.stream().map(FilledSlot::name).distinct().count();

        if (distinctSlots != placed.size())
            throw new RuntimeException("Cada lugar aceita só uma peça");
    }

    /** O lugar onde a peça caiu é o lugar onde ela deveria cair? */
    private CorrectionResult checkSlots(List<FilledSlot> placed, Map<Long, Alternative> pieces) {

        List<String> lstWrongSlots = new ArrayList<>();

        for (FilledSlot slot : placed) {
            Alternative piece = pieces.get(slot.alternativeId());

            if (!piece.getCorrectSlot().equals(slot.name()))
                lstWrongSlots.add(slot.name());
        }

        return new CorrectionResult(lstWrongSlots.isEmpty(), lstWrongSlots);
    }

    // ==================================================================
    // Atividades de marcar alternativa — a lógica que você já tinha
    // ==================================================================

    public CorrectionResult byAlternatives(Question question, AttemptAlternativeRequest obj) {

        List<Alternative> lstAlternative =
                this.alternativeService.getAlternatives(obj.lstAlternativeId());

        int allCorrects = 0;
        boolean missQuestion = false;

        for (Alternative alternative : lstAlternative) {
            if (alternative.isCorrect())
                allCorrects++;
            else
                missQuestion = true;
        }

        Long totalCorrects =
                this.alternativeService.countCorrectAlternativesByQuestion(obj.questionId());

        boolean correctQuestion = !missQuestion && allCorrects == totalCorrects;

        // Questão de marcar não tem "lugar errado" — a lista vai vazia.
        return new CorrectionResult(correctQuestion, List.of());
    }

    // ==================================================================

    private QuestionContent readContent(Question question) {
        try {
            return this.objectMapper.readValue(question.getContent(), QuestionContent.class);
        } catch (Exception e) {
            throw new RuntimeException("Conteúdo mal formado na questão " + question.getId(), e);
        }
    }
}
