package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.NotificacaoResponse;
import br.com.rodrigo.gestortarefas.api.repository.NotificacaoRepository;
import br.com.rodrigo.gestortarefas.api.services.INotificacao;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements INotificacao {

    private final NotificacaoRepository notificacaoRepository;

    @Override
    public Page<NotificacaoResponse> buscarNotificacoesNaoLidas(int page, int size, Usuario usuario) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "criadoEm"));
        Page<Notificacao> notificacoesPage = notificacaoRepository.findByUsuarioAndLidaFalse(usuario, pageable);
        return notificacoesPage.map(notificacao -> ModelMapperUtil.map(notificacao, NotificacaoResponse.class));
    }

    @Override
    public NotificacaoResponse criarNotificacao(Usuario usuario, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setLida(false);
        notificacao = notificacaoRepository.save(notificacao);
        return ModelMapperUtil.map(notificacao, NotificacaoResponse.class);
    }

    @Override
    public void marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.
                        NOTIFICACAO_NAO_ENCONTRADA_POR_ID.getMessage(id)));
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }
}
