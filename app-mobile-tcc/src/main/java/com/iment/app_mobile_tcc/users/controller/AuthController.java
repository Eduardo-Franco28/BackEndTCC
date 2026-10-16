package com.iment.app_mobile_tcc.users.controller;

import com.iment.app_mobile_tcc.shared.security.TeoCodeStore;
import com.iment.app_mobile_tcc.shared.security.TokenService;
import com.iment.app_mobile_tcc.users.dto.request.*;
import com.iment.app_mobile_tcc.users.dto.response.AuthResponse;
import com.iment.app_mobile_tcc.users.dto.response.TeoCodeResponse;
import com.iment.app_mobile_tcc.users.dto.response.UserResponse;
import com.iment.app_mobile_tcc.users.entity.User;
import com.iment.app_mobile_tcc.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final int PIN_MAX_ATTEMPTS = 5;

    private static final Duration PIN_LOCK_TIME = Duration.ofMinutes(2);

    private static final Duration TEO_SESSION_TIME = Duration.ofHours(8);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeoCodeStore teoCodeStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserByToken(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = this.authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = this.tokenService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Validated RegisterRequest request){
        if(this.userRepository.findByEmail(request.email()).isPresent()) return ResponseEntity.badRequest().build();
        User newUser = new User();

        newUser.setPassword(this.passwordEncoder.encode(request.password()));
        newUser.setNome(request.name());
        newUser.setEmail(request.email());
        newUser.setPin(this.passwordEncoder.encode(request.pin()));

            this.userRepository.save(newUser);
    
            String token = this.tokenService.generateToken(newUser);
    
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, UserResponse.from(newUser)));
        }

    @PostMapping("/pin")
    public ResponseEntity<Boolean> verifyPin(@RequestBody PinRequest request, @AuthenticationPrincipal User user){
        if(user.getPin() == null)
            return ResponseEntity.ok(false);

        if(this.isPinLocked(user))
            return this.pinLockedResponse(user);

        boolean valid = this.checkPin(user, request.pin());

        if(this.isPinLocked(user))
            return this.pinLockedResponse(user);

        return ResponseEntity.ok(valid);
    }

    @PostMapping("/teo/code")
    public ResponseEntity<TeoCodeResponse> createTeoCode(@RequestBody PinRequest request, @AuthenticationPrincipal User user){
        if(user.getPin() == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if(this.isPinLocked(user))
            return this.pinLockedResponse(user);

        boolean valid = this.checkPin(user, request.pin());

        if(this.isPinLocked(user))
            return this.pinLockedResponse(user);

        if(!valid)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(new TeoCodeResponse(this.teoCodeStore.create(user.getId())));
    }

    @PostMapping("/teo/exchange")
    public ResponseEntity<AuthResponse> exchangeTeoCode(@RequestBody TeoCodeRequest request){
        Long userId = this.teoCodeStore.consume(request.code());

        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = this.userRepository.findById(userId).orElse(null);

        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = this.tokenService.generateTeoToken(user, Instant.now());

        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    @PostMapping("/teo/refresh")
    public ResponseEntity<AuthResponse> refreshTeoToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @AuthenticationPrincipal User user){
        Instant pinAt = this.tokenService.getPinAt(authorization.replace("Bearer ", ""));

        if(pinAt == null || Instant.now().isAfter(pinAt.plus(TEO_SESSION_TIME)))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = this.tokenService.generateTeoToken(user, pinAt);

        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    private boolean isPinLocked(User user){
        return user.getPinLockedUntil() != null && Instant.now().isBefore(user.getPinLockedUntil());
    }

    private <T> ResponseEntity<T> pinLockedResponse(User user){
        long secondsLeft = Duration.between(Instant.now(), user.getPinLockedUntil()).toSeconds() + 1;

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(secondsLeft))
                .build();
    }

    private boolean checkPin(User user, String pin){
        if(this.passwordEncoder.matches(pin, user.getPin())){
            if(user.getPinFailedAttempts() > 0){
                user.setPinFailedAttempts(0);
                this.userRepository.save(user);
            }

            return true;
        }

        int attempts = user.getPinFailedAttempts() + 1;

        if(attempts >= PIN_MAX_ATTEMPTS){
            user.setPinFailedAttempts(0);
            user.setPinLockedUntil(Instant.now().plus(PIN_LOCK_TIME));
        } else {
            user.setPinFailedAttempts(attempts);
        }

        this.userRepository.save(user);

        return false;
    }

    @PatchMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody NewProfileRequest request){
        if(request.currentPassword() == null || request.currentPassword().isBlank())
            throw new RuntimeException("Informe a senha atual");

        if(request.nome() == null && request.email() == null)
            throw new RuntimeException("Informe um dado para alterar");

        if (request.email() != null && !request.email().isBlank()) {
            var existingUser = this.userRepository.findByEmail(request.email());

            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId()))
                throw new RuntimeException("Esse email já está em uso");
        }

        if(!this.passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            throw new RuntimeException("A senha atual está incorreta");

        if(request.nome() != null && !request.nome().isBlank())
            user.setNome(request.nome());

        if(request.email() != null && !request.email().isBlank())
            user.setEmail(request.email());

        this.userRepository.save(user);

        String token = this.tokenService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    @PatchMapping("/password")
    public ResponseEntity<AuthResponse> updatePassword(@AuthenticationPrincipal User user, @RequestBody NewPasswordRequest request){
        if(request.currentPassword() == null || request.currentPassword().isBlank())
            throw new RuntimeException("Informe a senha atual");

        if(!this.passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            throw new RuntimeException("A senha atual está incorreta");

        if(request.newPassword() == null && request.newPassword().isBlank())
            throw new RuntimeException("Informe a nova senha");

        user.setPassword(this.passwordEncoder.encode(request.newPassword()));

        this.userRepository.save(user);

        String token = this.tokenService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }
}
