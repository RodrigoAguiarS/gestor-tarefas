package br.com.rodrigo.gestortarefas.api.auth;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.LoginForm;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;

    private final AuthenticationManager authenticationManager;

    public Usuario authenticate(LoginForm loginUsuario) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUsuario.getEmail(),
                        loginUsuario.getSenha()
                )
        );

        return usuarioRepository.findByEmailIgnoreCase(loginUsuario.getEmail())
                .orElseThrow();
    }
}
