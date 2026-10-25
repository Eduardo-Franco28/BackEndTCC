package com.iment.app_mobile_tcc.drafts.dto.request;

import java.util.List;

public record GroupRequest(String name, List<ItemRequest> items) {
}
