package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoForm {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private BigDecimal preco;

    @NotBlank
    @Size(max = 500)
    private String descricao;

    @NotBlank
    private String codigoBarras;

    @NotNull
    private Integer quantidade;

    private List<String> arquivosUrl;

    @NotNull
    private Long categoria;
}