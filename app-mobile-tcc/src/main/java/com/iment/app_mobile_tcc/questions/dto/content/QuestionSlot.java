package com.iment.app_mobile_tcc.questions.dto.content;

/**
 * Um lugar do tabuleiro, do ponto de vista da QUESTAO.
 *
 * @param name  a chave do lugar. No mapa e no corpo casa com o nome do slot do
 *              board; na palavra e a posicao da letra ("1", "2", "3"); nas
 *              zonas e o nome do cenario ("mar").
 * @param blank true quando a crianca precisa preencher.
 * @param label o texto que a tela mostra nesse lugar: a letra "V" que ja vem
 *              pronta, ou o nome "MAR" do cenario. Nos lugares vazios de uma
 *              palavra fica nulo — mandar a letra seria entregar a resposta.
 */
public record QuestionSlot(String name, boolean blank, String label) {
}
