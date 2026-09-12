package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.progress.dto.correction.CorrectionResult;
import com.iment.app_mobile_tcc.progress.dto.request.AttemptAlternativeRequest;
import com.iment.app_mobile_tcc.questions.dto.content.QuestionSlotContent;
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
        QuestionSlotContent content = this.readContent(question);

        Set<String> emptySlots = this.getEmptySlots(content);
        Map<Long, Alternative> pieces = this.getPiecesById(question);

        // Um mesmo lugar aceita mais de uma peça? É o caso dos animais, onde o
        // mar recebe golfinho e peixe. Muda duas regras daqui pra baixo.
        boolean allowMultiple = Boolean.TRUE.equals(content.allowMultiple());

        this.validateSlots(placed, emptySlots, pieces, allowMultiple);

        return this.checkSlots(placed, pieces);
    }

    /** Os lugares que estavam vazios e a criança precisava preencher. */
    private Set<String> getEmptySlots(QuestionSlotContent content) {
        return content.slots().stream()
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
                               Map<Long, Alternative> pieces, boolean allowMultiple) {

        if (placed == null || placed.isEmpty())
            throw new RuntimeException("Nenhuma peça foi colocada");

        this.validateComplete(placed, emptySlots, pieces, allowMultiple);

        for (FilledSlot slot : placed) {
            Alternative piece = pieces.get(slot.alternativeId());

            // Peça de outra questão. Sem isto dá pra confundir a correção.
            if (piece == null)
                throw new RuntimeException("Essa peça não pertence a esta questão");

            // Peça que deveria ter lugar e não tem: erro de quem criou a questão.
            // Já uma peça com correct=false e sem gabarito é DISTRATORA: ela
            // existe pra ser errada, como as letras que não entram na palavra.
            if (piece.isCorrect() && piece.getCorrectSlot() == null)
                throw new RuntimeException("Peça sem gabarito: " + piece.getDescription());

            // Lugar que não existe, ou que já vinha preenchido (o tronco).
            if (!emptySlots.contains(slot.name()))
                throw new RuntimeException("Lugar inválido ou já preenchido: " + slot.name());
        }

        this.validateOnePiecePerSlot(placed, allowMultiple);
    }

    /**
     * A criança terminou de montar?
     *
     * Com um lugar por peça (palavra, mapa, corpo), terminar é ter preenchido
     * todo buraco vazio. As letras distratoras ficam de fora e tudo bem.
     *
     * Quando um lugar aceita várias peças, o número de peças não tem relação
     * nenhuma com o de lugares — são 6 animais em 3 cenários. Aí terminar é ter
     * usado todas as peças.
     */
    private void validateComplete(List<FilledSlot> placed, Set<String> emptySlots,
                                  Map<Long, Alternative> pieces, boolean allowMultiple) {

        int esperado = allowMultiple ? pieces.size() : emptySlots.size();

        if (placed.size() != esperado)
            throw new RuntimeException("Faltam peças para completar");
    }

    /**
     * Duas peças no mesmo lugar.
     * Proibido no corpo, no mapa e na palavra; permitido nos animais, onde o mar
     * recebe golfinho e peixe.
     *
     * A permissão vem do `content` da questão, não do board: os animais não têm
     * board nenhum, e ainda assim precisam da regra. O Board.allowMultiple
     * continua na tabela mas ninguém mais o lê.
     */
    private void validateOnePiecePerSlot(List<FilledSlot> placed, boolean allowMultiple) {

        if (allowMultiple)
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

            // slot.name() vem na frente de propósito: se o gabarito da peça for
            // null (distratora), o equals dá false e ela conta como errada —
            // que é exatamente o que ela deve ser.
            if (!slot.name().equals(piece.getCorrectSlot()))
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

    private QuestionSlotContent readContent(Question question) {
        try {
            return this.objectMapper.readValue(question.getContent(), QuestionSlotContent.class);
        } catch (Exception e) {
            throw new RuntimeException("Conteúdo mal formado na questão " + question.getId(), e);
        }
    }
}
