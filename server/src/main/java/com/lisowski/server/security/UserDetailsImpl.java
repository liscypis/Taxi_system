package com.lisowski.server.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lisowski.server.models.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor(access=AccessLevel.PUBLIC)
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Getter(value = AccessLevel.PUBLIC)
    private Long id;
    @Getter(value = AccessLevel.PUBLIC)
    private String name;
    private String surname;
    private String userName;
    @JsonIgnore
    private String password;
    @Getter(value = AccessLevel.PUBLIC)
    private String email;
    @Getter(value = AccessLevel.PUBLIC)
    private String phoneNum;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoneNum(),
                authorities
        );
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
