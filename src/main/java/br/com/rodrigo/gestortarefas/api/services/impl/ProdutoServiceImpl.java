package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.CategoriaMapper;
import br.com.rodrigo.gestortarefas.api.conversor.ProdutoMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.EstoqueService;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoProduto;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoVenda;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.ICategoria;
import br.com.rodrigo.gestortarefas.api.services.IProduto;
import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements IProduto {

    private final ICategoria categoriaService;
    private final ProdutoRepository produtoRepository;
    private final S3StorageService s3StorageService;
    private final ValidadorUtil validadorUtil;
    private final EstoqueService estoqueService;

    @Override
    public ProdutoResponse criar(Long id, ProdutoForm produtoForm) {
        Produto produto = criaProduto(produtoForm, id);
        produto = produtoRepository.save(produto);
        movimentarEstoqueProduto(produto, produtoForm.getQuantidade(), id);
        return construirDto(produto);
    }

    @Override
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(id)));

        if (isNotEmpty(produto.getArquivosUrl())) {
            for (String fileUrl : produto.getArquivosUrl()) {
                s3StorageService.apagarArquivo(fileUrl);
            }
        }

        produtoRepository.delete(produto);
    }

    @Override
    public Optional<ProdutoResponse> consultarPorId(Long id) {
        return produtoRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(id)));
    }

    @Override
    public Page<ProdutoResponse> listarTodos(int page, int size, String sort, Long id, String nome, String descricao,
                                             BigDecimal preco, String codigoBarras, Integer quantidade, Long categoriaId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Produto> produtos = produtoRepository.findAll(id, nome, descricao, preco, codigoBarras, quantidade, categoriaId, pageable);
        return produtos.map(this::construirDto);
    }

    @Override
    public List<Produto> buscarPorIds(List<Long> produtoIds) {
        if (produtoIds != null && !produtoIds.isEmpty()) {
            return produtoRepository.findAllById(produtoIds);
        }
        return List.of();
    }

    private Produto criaProduto(ProdutoForm produtoForm, Long id) {
        Produto produto = id != null ? produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(id))) : new Produto();

        validadorUtil.validarNomeProduto(produtoForm.getNome(), id);
        validadorUtil.validarCodigoBarras(produtoForm.getCodigoBarras(), id);

        CategoriaResponse response = categoriaService.consultarPorId(produtoForm.getCategoriaId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CATEGORIA_NAO_ENCONTRADA.getMessage(produtoForm.getCategoriaId())));

        Categoria categoria = CategoriaMapper.responseParaEntidade(response);

        produto.setNome(produtoForm.getNome());
        produto.setDescricao(produtoForm.getDescricao());
        produto.setPreco(produtoForm.getPreco());
        produto.setCodigoBarras(produtoForm.getCodigoBarras());
        produto.setQuantidade(produtoForm.getQuantidade());
        produto.setCategoria(categoria);
        produto.setArquivosUrl(produtoForm.getArquivosUrl());

        return produto;
    }
    @Override
    public List<GraficoVenda> obterVendasParaGrafico() {
        return produtoRepository.findVendasParaGrafico(Status.CANCELADO);
    }

    @Override
    public List<GraficoVenda> obterVendasPorCategoria() {
        return produtoRepository.findVendasPorCategoria(Status.CANCELADO);
    }

    @Override
    public List<GraficoProduto> obterFaturamentoPorProduto() {
        return produtoRepository.findFaturamentoPorProduto(Status.CANCELADO);
    }

    private ProdutoResponse construirDto(Produto produto) {
        return ProdutoMapper.entidadeParaResponse(produto);

    }

    private void movimentarEstoqueProduto(Produto produto, int quantidade, Long idProduto) {
        TipoMovimentacao tipo = (idProduto == null) ? TipoMovimentacao.ENTRADA : TipoMovimentacao.AJUSTE;
        OrigemMovimentacao origem = OrigemMovimentacao.PRODUTO;
        AcaoMovimentacao acao = (idProduto == null) ? AcaoMovimentacao.CRIACAO_PRODUTO : AcaoMovimentacao.EDICAO_PRODUTO;

        estoqueService.processarMovimentacaoProduto(produto, quantidade, tipo, origem, acao, produto.getId());
    }
}
