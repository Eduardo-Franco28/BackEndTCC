package com.iment.app_mobile_tcc.questions.dto.content;

import java.util.List;

/**
 * O conteudo da questao, lido do campo `content`.
 *
 * @param slots         quais lugares existem e quais estao vazios.
 * @param hint          emoji ou texto de apoio. Hoje so a palavra usa.
 * @param allowMultiple true quando um mesmo lugar aceita mais de uma peca, como
 *                      o mar que recebe golfinho e peixe. Fica aqui, e nao no
 *                      Board, porque e regra DESTA questao — e porque uma
 *                      atividade pode nao ter board nenhum.
 *                      Nulo vale como false.
 */
public record QuestionSlotContent(List<QuestionSlot> slots, String hint, Boolean allowMultiple) {
}
