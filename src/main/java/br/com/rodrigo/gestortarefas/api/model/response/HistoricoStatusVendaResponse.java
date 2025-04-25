package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class HistoricoStatusVendaResponse {
    private Long id;
    private StatusResponse status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String observacao;
}
