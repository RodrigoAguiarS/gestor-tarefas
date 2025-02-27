package br.com.rodrigo.gestortarefas.api.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ImpersonateAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public ImpersonateAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
    }
}