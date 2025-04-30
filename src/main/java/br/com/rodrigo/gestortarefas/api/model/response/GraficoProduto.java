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
public class GraficoProduto {
    private String nome;
    private BigDecimal valor;

    public GraficoProduto(String nome, Long valor) {
        this.nome = nome;
        this.valor = BigDecimal.valueOf(valor);
    }
}
