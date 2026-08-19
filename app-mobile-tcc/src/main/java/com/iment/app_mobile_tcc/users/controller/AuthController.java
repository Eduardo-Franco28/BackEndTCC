package com.iment.app_mobile_tcc.users.controller;

import com.iment.app_mobile_tcc.auth.security.TokenService;
import com.iment.app_mobile_tcc.users.dto.request.UserPasswordRequest;
import com.iment.app_mobile_tcc.users.dto.request.UserProfileRequest;
import com.iment.app_mobile_tcc.users.dto.request.LoginRequest;
import com.iment.app_mobile_tcc.users.dto.request.RegisterRequest;
import com.iment.app_mobile_tcc.users.dto.response.AuthResponse;
import com.iment.app_mobile_tcc.users.dto.response.UserResponse;
import com.iment.app_mobile_tcc.users.entity.User;
import com.iment.app_mobile_tcc.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

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

        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, UserResponse.from(newUser)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody UserProfileRequest request){
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
    public ResponseEntity<AuthResponse> updatePassword(@AuthenticationPrincipal User user, @RequestBody UserPasswordRequest request){
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
