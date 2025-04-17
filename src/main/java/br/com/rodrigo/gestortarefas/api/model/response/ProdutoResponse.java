package br.com.rodrigo.gestortarefas.api.model.response;

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
public class ProdutoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String codigoBarras;
    private Integer quantidade;
    private BigDecimal preco;
    private CategoriaResponse categoria;
    private List<String> arquivosUrl;
}
