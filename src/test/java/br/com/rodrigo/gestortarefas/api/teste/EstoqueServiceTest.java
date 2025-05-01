package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.MovimentacaoEstoque;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.repository.MovimentacaoEstoqueRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.estoque.EstoqueService;
import br.com.rodrigo.gestortarefas.api.services.estoque.ItemVendaStrategy;
import br.com.rodrigo.gestortarefas.api.services.estoque.MovimentacaoFactory;
import br.com.rodrigo.gestortarefas.api.services.estoque.ProdutoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private MovimentacaoFactory movimentacaoFactory;

    @InjectMocks
    private EstoqueService estoqueService;

    @Captor
    private ArgumentCaptor<MovimentacaoEstoque> movimentacaoCaptor;

    private Produto produto;
    private ItemVenda itemVenda;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setQuantidade(10);
        produto.setPreco(BigDecimal.valueOf(100.00));

        itemVenda = new ItemVenda();
        itemVenda.setProduto(produto);
        itemVenda.setQuantidade(10);
    }

    @Test
    void quandoCriarProduto_deveGerarMovimentacaoEntrada() {
        produto.setId(null);

        when(movimentacaoFactory.criarStrategy(any(Produto.class)))
                .thenReturn(new ProdutoStrategy(produtoRepository));

        estoqueService.processarMovimentacao(produto, 10, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.CRIACAO_PRODUTO, null);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(10, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(AcaoMovimentacao.CRIACAO_PRODUTO, movimentacao.getAcao());
    }

    @Test
    void quandoAtualizarProdutoComAumento_deveGerarMovimentacaoEntrada() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(Produto.class)))
                .thenReturn(new ProdutoStrategy(produtoRepository));

        estoqueService.processarMovimentacao(produto, 15, TipoMovimentacao.AJUSTE,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(5, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(15, produto.getQuantidade());
    }

    @Test
    void quandoAtualizarProdutoComReducao_deveGerarMovimentacaoSaida() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(Produto.class)))
                .thenReturn(new ProdutoStrategy(produtoRepository));

        estoqueService.processarMovimentacao(produto, 7, TipoMovimentacao.AJUSTE,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(3, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.SAIDA, movimentacao.getTipo());
        assertEquals(7, produto.getQuantidade());
    }

    @Test
    void quandoProcessarItemVenda_deveGerarMovimentacaoSaida() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        estoqueService.processarMovimentacao(itemVenda, 2, TipoMovimentacao.SAIDA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(2, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.SAIDA, movimentacao.getTipo());
        assertEquals(8, produto.getQuantidade());
    }

    @Test
    void quandoQuantidadeInsuficiente_deveLancarExcecao() {
        produto.setQuantidade(5);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        itemVenda.setQuantidade(10);

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        assertThrows(ViolacaoIntegridadeDadosException.class, () ->
                estoqueService.processarMovimentacao(itemVenda, 10, TipoMovimentacao.SAIDA,
                        OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L));

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }


    @Test
    void quandoAtualizarProdutoSemAlterarQuantidade_naoDeveGerarMovimentacao() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(Produto.class)))
                .thenReturn(new ProdutoStrategy(produtoRepository));

        estoqueService.processarMovimentacao(produto, 10, null,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void quandoTipoItemInvalido_deveLancarExcecao() {
        String itemInvalido = "Item inválido";

        when(movimentacaoFactory.criarStrategy(any(String.class)))
                .thenThrow(new IllegalArgumentException("Tipo de item inválido"));

        assertThrows(IllegalArgumentException.class, () ->
                estoqueService.processarMovimentacao(itemInvalido, 10, TipoMovimentacao.ENTRADA,
                        OrigemMovimentacao.PRODUTO, AcaoMovimentacao.CRIACAO_PRODUTO, 1L));

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void quandoEstornarVenda_deveRetornarItensAoEstoque() {

        produto.setQuantidade(8);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        estoqueService.processarMovimentacao(itemVenda, 2, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(2, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(OrigemMovimentacao.VENDA, movimentacao.getOrigem());
        assertEquals(AcaoMovimentacao.ESTORNO_VENDA, movimentacao.getAcao());
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void quandoEstornarMultiplosItensVenda_deveRetornarTodosAoEstoque() {
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setQuantidade(8);
        produto1.setNome("Produto 1");

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setQuantidade(15);
        produto2.setNome("Produto 2");

        ItemVenda item1 = new ItemVenda();
        item1.setProduto(produto1);
        item1.setQuantidade(2);

        ItemVenda item2 = new ItemVenda();
        item2.setProduto(produto2);
        item2.setQuantidade(5);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto1));
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(produto2));

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        estoqueService.processarMovimentacao(item1, 2, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L);

        estoqueService.processarMovimentacao(item2, 5, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L);

        verify(movimentacaoRepository, times(2)).save(movimentacaoCaptor.capture());
        List<MovimentacaoEstoque> movimentacoes = movimentacaoCaptor.getAllValues();

        assertEquals(2, movimentacoes.get(0).getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacoes.get(0).getTipo());
        assertEquals(10, produto1.getQuantidade());

        assertEquals(5, movimentacoes.get(1).getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacoes.get(1).getTipo());
        assertEquals(20, produto2.getQuantidade());
    }

    @Test
    void quandoEstornarVendaComQuantidadeZero_deveLancarExcecao() {
        produto.setQuantidade(8);
        itemVenda.setQuantidade(0);

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        assertThrows(IllegalArgumentException.class, () ->
                estoqueService.processarMovimentacao(itemVenda, 0, TipoMovimentacao.ENTRADA,
                        OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L));

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void quandoEstornarVendaComQuantidadeNegativa_deveLancarExcecao() {
        produto.setQuantidade(8);
        itemVenda.setQuantidade(-5);

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        assertThrows(IllegalArgumentException.class, () ->
                estoqueService.processarMovimentacao(itemVenda, -5, TipoMovimentacao.ENTRADA,
                        OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L));

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void quandoAtualizarProdutoComQuantidadeMaxima_deveProcessarCorretamente() {
        int quantidadeMaxima = Integer.MAX_VALUE - 100;
        produto.setQuantidade(100);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        when(movimentacaoFactory.criarStrategy(any(Produto.class)))
                .thenReturn(new ProdutoStrategy(produtoRepository));

        estoqueService.processarMovimentacao(produto, quantidadeMaxima, null,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(quantidadeMaxima - 100, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(quantidadeMaxima, produto.getQuantidade());
    }

    @Test
    void quandoProcessarMultiplasMovimentacoes_deveManterConsistencia() {
        produto.setQuantidade(100);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ItemVendaStrategy itemVendaStrategy = new ItemVendaStrategy(produtoRepository);
        ProdutoStrategy produtoStrategy = new ProdutoStrategy(produtoRepository);

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class))).thenReturn(itemVendaStrategy);
        when(movimentacaoFactory.criarStrategy(any(Produto.class))).thenReturn(produtoStrategy);

        estoqueService.processarMovimentacao(itemVenda, 20, TipoMovimentacao.SAIDA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);

        estoqueService.processarMovimentacao(produto, 90, null,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        verify(movimentacaoRepository, times(2)).save(movimentacaoCaptor.capture());
        List<MovimentacaoEstoque> movimentacoes = movimentacaoCaptor.getAllValues();

        assertEquals(20, movimentacoes.get(0).getQuantidade());
        assertEquals(TipoMovimentacao.SAIDA, movimentacoes.get(0).getTipo());

        assertEquals(10, movimentacoes.get(1).getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacoes.get(1).getTipo());
    }

    @Test
    void quandoEstornarVendaComDevolucaoParcial_deveAtualizarEstoqueCorretamente() {
        produto.setQuantidade(5);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        itemVenda.setQuantidade(3);

        when(movimentacaoFactory.criarStrategy(any(ItemVenda.class)))
                .thenReturn(new ItemVendaStrategy(produtoRepository));

        estoqueService.processarMovimentacao(itemVenda, 3, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.ESTORNO_VENDA, 1L);

        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacao = movimentacaoCaptor.getValue();
        assertEquals(3, movimentacao.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacao.getTipo());
        assertEquals(8, produto.getQuantidade());
    }
}