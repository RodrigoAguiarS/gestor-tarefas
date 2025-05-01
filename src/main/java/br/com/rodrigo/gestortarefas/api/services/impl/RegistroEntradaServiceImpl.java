package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.model.RegistroEntrada;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.RegistroEntradaRepository;
import br.com.rodrigo.gestortarefas.api.services.IRegistroEntrada;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistroEntradaServiceImpl implements IRegistroEntrada {

    private final RegistroEntradaRepository registroEntradaRepository;

    private final IUsuario usuarioService;

    @Override
    public void registrarEntrada(Usuario usuario, String ipAddress) {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setUsuario(usuario);
        registroEntrada.setDataEntrada(LocalDateTime.now());
        registroEntrada.setIpAddress(ipAddress);
        registroEntradaRepository.save(registroEntrada);
    }

    @Override
    public RegistroEntrada obterUltimoRegistroEntrada() {
        UsuarioResponse usuario = usuarioService.obterUsuarioLogado();
        return registroEntradaRepository.findTopByUsuarioIdOrderByDataEntradaDesc(usuario.getId());
    }
}
