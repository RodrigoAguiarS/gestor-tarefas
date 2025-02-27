package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.services.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @MessageMapping("/notificacao")
    @SendTo("/topic/notificacoes")
    public Notificacao enviarNotificacao(Notificacao notificacao) {
        Usuario usuario = notificacao.getUsuario();
        return notificacaoService.criarNotificacao(usuario, notificacao.getMensagem());
    }
}
