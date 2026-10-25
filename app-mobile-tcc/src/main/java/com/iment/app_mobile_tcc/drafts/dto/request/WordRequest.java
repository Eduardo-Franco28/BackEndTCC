package com.iment.app_mobile_tcc.drafts.dto.request;

import java.util.List;

public record WordRequest(String word, List<Integer> blanks, String emoji) {
}
