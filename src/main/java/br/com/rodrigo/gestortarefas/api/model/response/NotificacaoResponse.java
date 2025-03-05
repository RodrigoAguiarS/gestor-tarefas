package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoResponse {

    private Long id;
    private String mensagem;
    private boolean lida;
    private UsuarioResponse usuario;
    private LocalDateTime criadoEm;
}
