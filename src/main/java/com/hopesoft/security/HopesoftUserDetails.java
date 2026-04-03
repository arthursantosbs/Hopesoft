package com.hopesoft.security;

import com.hopesoft.dto.AuthenticatedUserResponse;
import com.hopesoft.model.Usuario;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class HopesoftUserDetails implements UserDetails {

    private final Long id;
    private final Long empresaId;
    private final String nome;
    private final String email;
    private final String senha;
    private final String perfil;
    private final List<GrantedAuthority> authorities;

    private HopesoftUserDetails(
            Long id,
            Long empresaId,
            String nome,
            String email,
            String senha,
            String perfil,
            List<GrantedAuthority> authorities
    ) {
        this.id = id;
        this.empresaId = empresaId;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.authorities = authorities;
    }

    public static HopesoftUserDetails fromUsuario(Usuario usuario) {
        String perfil = usuario.getPerfil().name();
        return new HopesoftUserDetails(
                usuario.getId(),
                usuario.getEmpresa().getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                perfil,
                List.of(new SimpleGrantedAuthority("ROLE_" + perfil))
        );
    }

    public Long getId() {
        return id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getNome() {
        return nome;
    }

    public String getPerfil() {
        return perfil;
    }

    public AuthenticatedUserResponse toResponse() {
        return new AuthenticatedUserResponse(id, empresaId, nome, email, perfil);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
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
