package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.EstoqueService;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.MovimentacaoEstoque;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.repository.MovimentacaoEstoqueRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Produto produto;
    private ItemVenda itemVenda;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setQuantidade(10);

        itemVenda = new ItemVenda();
        itemVenda.setProduto(produto);
        itemVenda.setQuantidade(5);
    }

    @Test
    void deveDefinirQuantidadeDiretamenteQuandoCriacaoProduto() {
        estoqueService.processarMovimentacaoProduto(produto, 20, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.CRIACAO_PRODUTO, 1L);

        assertEquals(20, produto.getQuantidade());
        verify(produtoRepository).save(produto);

        ArgumentCaptor<MovimentacaoEstoque> movimentacaoCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movimentacaoRepository).save(movimentacaoCaptor.capture());

        MovimentacaoEstoque movimentacaoSalva = movimentacaoCaptor.getValue();
        assertEquals(20, movimentacaoSalva.getQuantidade());
        assertEquals(TipoMovimentacao.ENTRADA, movimentacaoSalva.getTipo());
        assertEquals(OrigemMovimentacao.PRODUTO, movimentacaoSalva.getOrigem());
        assertEquals(AcaoMovimentacao.CRIACAO_PRODUTO, movimentacaoSalva.getAcao());
    }

    @Test
    void deveAjustarEstoqueQuandoEdicaoProduto() {
        estoqueService.processarMovimentacaoProduto(produto, 15, TipoMovimentacao.AJUSTE,
                OrigemMovimentacao.PRODUTO, AcaoMovimentacao.EDICAO_PRODUTO, 1L);

        assertEquals(25, produto.getQuantidade());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveReduzirEstoqueQuandoMovimentacaoSaidaComEstoqueSuficiente() {
        estoqueService.processarMovimentacaoProduto(produto, 5, TipoMovimentacao.SAIDA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);

        assertEquals(5, produto.getQuantidade());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveLancarExcecaoQuandoMovimentacaoSaidaComEstoqueInsuficiente() {
        assertThrows(ViolacaoIntegridadeDadosException.class, () -> {
            estoqueService.processarMovimentacaoProduto(produto, 15, TipoMovimentacao.SAIDA,
                    OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);
        });

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }


    @Test
    void deveProcessarMovimentacaoDeItemVendaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        estoqueService.processarMovimentacao(itemVenda, TipoMovimentacao.SAIDA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);

        assertEquals(5, produto.getQuantidade());
        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficienteParaItemVenda() {
        produto.setQuantidade(3);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(ViolacaoIntegridadeDadosException.class, () -> {
            estoqueService.processarMovimentacao(itemVenda, TipoMovimentacao.SAIDA,
                    OrigemMovimentacao.VENDA, AcaoMovimentacao.NOVA_VENDA, 1L);
        });

        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void deveProcessarCancelamentoDeVendaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        estoqueService.processarMovimentacao(itemVenda, TipoMovimentacao.ENTRADA,
                OrigemMovimentacao.VENDA, AcaoMovimentacao.CANCELAMENTO, 1L);

        assertEquals(15, produto.getQuantidade());
        verify(produtoRepository).save(produto);
        verify(movimentacaoRepository).save(any(MovimentacaoEstoque.class));
    }
}