package com.iment.app_mobile_tcc.users.entity;

import com.iment.app_mobile_tcc.users.enums.UserEnum;
import com.iment.app_mobile_tcc.users.enums.UserGrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
//    @Column(nullable = false)
//    private UserEnum role; to-do

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if(this.role == UserEnum.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        else return List.of(new SimpleGrantedAuthority("ROLE_USER")); to-do
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
