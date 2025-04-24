package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;

public class ProdutoMapper {

    public static ProdutoResponse entidadeParaResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getQuantidade(),
                produto.getPreco(),
                new CategoriaResponse(
                        produto.getCategoria().getId(),
                        produto.getCategoria().getNome(),
                        produto.getCategoria().getDescricao()),
                produto.getArquivosUrl()
        );
    }

    public static Produto formParaEntidade(ProdutoForm form, Categoria categoria) {
        return Produto.builder()
                .nome(form.getNome())
                .descricao(form.getDescricao())
                .codigoBarras(form.getCodigoBarras())
                .quantidade(form.getQuantidade())
                .preco(form.getPreco())
                .categoria(categoria)
                .arquivosUrl(form.getArquivosUrl())
                .build();
    }
}
