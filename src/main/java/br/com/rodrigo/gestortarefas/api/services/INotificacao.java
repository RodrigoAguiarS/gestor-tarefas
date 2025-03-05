package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.NotificacaoResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface INotificacao {
    Page<NotificacaoResponse> buscarNotificacoesNaoLidas(int page, int size, Usuario usuario);
    NotificacaoResponse criarNotificacao(Usuario usuario, String mensagem);
    void marcarComoLida(Long id);
}
