package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.NotificacaoResponse;
import br.com.rodrigo.gestortarefas.api.services.INotificacao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notificacao")
public class NotificacaoController extends ControllerBase<NotificacaoResponse> {

    private final INotificacao notificacaoService;

    @MessageMapping("/notificacao")
    public NotificacaoResponse enviarNotificacao(Notificacao notificacao) {
        Usuario usuario = notificacao.getUsuario();
        return notificacaoService.criarNotificacao(usuario, notificacao.getMensagem());
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<Page<NotificacaoResponse>> buscarNotificacoesNaoLidas(
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Page<NotificacaoResponse> notificacoes = notificacaoService.buscarNotificacoesNaoLidas(page, size, usuario);
        return responderListaDeItensPaginada(notificacoes);
    }

    @PutMapping("/{id}/marcar-como-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return responderSemConteudo();
    }
}