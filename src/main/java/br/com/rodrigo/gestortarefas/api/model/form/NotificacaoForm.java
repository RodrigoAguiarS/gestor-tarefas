package br.com.rodrigo.gestortarefas.api.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoForm {
    private String mensagem;
    private boolean lida;
    private Long usuarioId;
}
