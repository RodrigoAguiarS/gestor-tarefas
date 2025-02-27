package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public List<Notificacao> buscarNotificacoesNaoLidas(Usuario usuario) {
        return notificacaoRepository.findByUsuarioAndLidaFalse(usuario);
    }

    public Notificacao criarNotificacao(Usuario usuario, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setLida(false);
        return notificacaoRepository.save(notificacao);
    }

    public void marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id).orElseThrow();
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }
}
