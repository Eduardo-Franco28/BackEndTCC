package com.iment.app_mobile_tcc.shared.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TeoCodeStore {

    private static final Duration CODE_TIME = Duration.ofSeconds(60);

    private final SecureRandom random = new SecureRandom();

    private final Map<String, TeoCode> codes = new ConcurrentHashMap<>();

    public String create(Long userId){
        this.codes.values().removeIf(code -> code.isExpired());

        byte[] bytes = new byte[32];
        this.random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        this.codes.put(code, new TeoCode(userId, Instant.now().plus(CODE_TIME)));

        return code;
    }

    public Long consume(String code){
        if(code == null) return null;

        TeoCode teoCode = this.codes.remove(code);

        if(teoCode == null || teoCode.isExpired()) return null;

        return teoCode.userId();
    }

    private record TeoCode(Long userId, Instant expiresAt){
        boolean isExpired(){
            return Instant.now().isAfter(this.expiresAt);
        }
    }
}
