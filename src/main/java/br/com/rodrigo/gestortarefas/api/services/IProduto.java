package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Optional;

public interface IProduto {
    ProdutoResponse criar(ProdutoForm produtoForm);
    ProdutoResponse atualizar(Long id, ProdutoForm produtoForm);
    void deletar(Long id);
    Optional<ProdutoResponse> consultarPorId(Long id);
    Page<ProdutoResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                      String descricao, BigDecimal preco, String codigoBarras, Integer quantidade,
                                      Long categoriaId);
}
