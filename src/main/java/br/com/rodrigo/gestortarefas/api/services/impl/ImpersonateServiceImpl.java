package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.auth.ImpersonateAuthenticationToken;
import br.com.rodrigo.gestortarefas.api.auth.JwtService;
import br.com.rodrigo.gestortarefas.api.config.AutenticacaoCache;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.Impersonate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ImpersonateServiceImpl implements Impersonate {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AutenticacaoCache autenticacaoCache;

    @Override
    public String iniciarPersonificacao(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_LOGIN.getMessage(email)));

        UserDetails userDetails = User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(usuario.getAuthorities())
                .build();

        Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
        autenticacaoCache.salvar(email, originalAuthentication);

        SecurityContextHolder.getContext().setAuthentication(
                new ImpersonateAuthenticationToken(userDetails, usuario.getAuthorities()));

        return jwtService.generateToken(userDetails);
    }


    @Override
    public void encerrarPersonificacao(String email) {
        Authentication originalAuthentication = autenticacaoCache.recuperar(email);
        if (isEmpty(originalAuthentication)) {
            SecurityContextHolder.clearContext();
        }
        SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
        autenticacaoCache.remover(email);
    }
}
