package com.iment.app_mobile_tcc.progress.dto.correction;

import java.util.List;

/**
 * O veredito da correção, antes de virar resposta HTTP.
 *
 * Fica solto em dto/ e não em request/ ou response/ porque ele nunca sai do
 * servidor — só carrega o resultado do CorrectionService pro AttemptService.
 */
public record CorrectionResult(boolean correct, List<String> lstWrongSlots) {
}
