package com.iment.app_mobile_tcc.progress.dto.response;

import java.util.List;

public record AnsweredAlternativeResponse (boolean correct, boolean concluded, List<String> lstWrongSlots){
}
