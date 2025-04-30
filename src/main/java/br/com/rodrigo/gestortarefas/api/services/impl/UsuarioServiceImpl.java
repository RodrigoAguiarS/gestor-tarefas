package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.UsuarioMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuario {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioResponse obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UsuarioMapper.entidadeParaResponse(obterUsuarioPorEmail(authentication.getName()));
    }

    @Override
    public List<String> obterPerfis(String email) {
        Usuario usuario = obterUsuarioPorEmail(email);
        return usuario.getPerfis().stream().map(Perfil::getNome).collect(Collectors.toList());
    }

    private Usuario obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_LOGIN.getMessage(email)));
    }
}