package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final Map<Long, SseEmitter> emissores = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> mensagensPendentes = new ConcurrentHashMap<>();
    private final UsuarioRepository usuarioRepository;

    public SseEmitter conectar(Long usuarioId) {
        SseEmitter emitterExistente = emissores.get(usuarioId);
        if (emitterExistente != null) {
            try {
                emitterExistente.send(SseEmitter.event().comment("heartbeat"));
                return emitterExistente;
            } catch (Exception e) {
                log.debug("Removendo emitter inválido para usuário {}", usuarioId);
                removerConexao(usuarioId);
            }
        }

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        try {
            emissores.put(usuarioId, emitter);
            emitter.onTimeout(() -> removerConexao(usuarioId));
            emitter.onCompletion(() -> removerConexao(usuarioId));
            enviarMensagensPendentes(usuarioId);
        } catch (Exception e) {
            log.error("Erro ao configurar nova conexão para usuário {}: {}",
                    usuarioId, e.getMessage());
            removerConexao(usuarioId);
            throw e;
        }

        return emitter;
    }

    public void notificarUsuario(Long usuarioId, String mensagem) {
        SseEmitter emitter = emissores.get(usuarioId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("nova-notificacao")
                        .data(mensagem));
            } catch (IOException e) {
                log.error("Erro ao enviar notificação para usuário {}: {}", usuarioId, e.getMessage());
                salvarMensagemPendente(usuarioId, mensagem);
                removerConexao(usuarioId);
            }
        } else {
            salvarMensagemPendente(usuarioId, mensagem);
        }
    }

    public void notificarPorPerfil(String mensagem, long idPerfil) {
        List<Usuario> operadores = usuarioRepository.findAllByPerfisId(idPerfil);
        operadores.forEach(operador ->
                notificarUsuario(operador.getId(), mensagem)
        );
    }

    private void enviarMensagensPendentes(Long usuarioId) {
        List<String> mensagens = mensagensPendentes.remove(usuarioId);
        if (mensagens != null && !mensagens.isEmpty()) {
            SseEmitter emitter = emissores.get(usuarioId);
            if (emitter != null) {
                mensagens.forEach(mensagem -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("nova-notificacao")
                                .data(mensagem));
                    } catch (IOException e) {
                        log.error("Erro ao enviar mensagem pendente: {}", e.getMessage());
                    }
                });
            }
        }
    }

    private void salvarMensagemPendente(Long usuarioId, String mensagem) {
        mensagensPendentes.computeIfAbsent(usuarioId, k -> new ArrayList<>())
                .add(mensagem);
    }

    private void removerConexao(Long usuarioId) {
        SseEmitter emitter = emissores.remove(usuarioId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.error("Erro ao fechar conexão: {}", e.getMessage());
            }
        }
    }
}
