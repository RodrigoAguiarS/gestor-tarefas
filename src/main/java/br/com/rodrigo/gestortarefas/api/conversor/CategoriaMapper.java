package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.form.CategoriaForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;

public class CategoriaMapper {

    public static CategoriaResponse entidadeParaResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }

    public static Categoria formParaEntidade(CategoriaForm form) {
        return Categoria.builder()
                .nome(form.getNome())
                .descricao(form.getDescricao())
                .build();
    }

    public static Categoria responseParaEntidade(CategoriaResponse response) {
        Categoria categoria = new Categoria();
        categoria.setId(response.getId());
        categoria.setNome(response.getNome());
        categoria.setDescricao(response.getDescricao());
        return categoria;
    }
}
