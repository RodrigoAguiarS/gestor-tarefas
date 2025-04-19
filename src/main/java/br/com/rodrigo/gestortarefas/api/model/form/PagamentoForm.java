package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoForm {

    @NotBlank
    @Size(max = 50)
    private String nome;

    @NotBlank
    @Size(max = 255)
    private String descricao;

    @NotNull
    private BigDecimal porcentagemAcrescimo;
}
