package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal porcentagemAcrescimo;
    private Boolean ativo;
}
