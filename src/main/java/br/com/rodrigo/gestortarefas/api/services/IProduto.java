package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoProduto;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoVenda;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProduto {
    ProdutoResponse criar(Long idProduto, ProdutoForm produtoForm);
    void deletar(Long id);
    Optional<ProdutoResponse> consultarPorId(Long id);
    Produto buscarPorId(Long id);
    Page<ProdutoResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                      String descricao, BigDecimal preco, String codigoBarras, Integer quantidade,
                                      Long categoriaId);

    List<Produto> buscarPorIds(List<Long> produtoIds);

    List<GraficoVenda> obterVendasParaGrafico();

    List<GraficoVenda> obterVendasPorCategoria();

    List<GraficoProduto> obterFaturamentoPorProduto();
}
