package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import br.com.rodrigo.gestortarefas.api.repository.VendaRepository;
import br.com.rodrigo.gestortarefas.api.services.impl.VendaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendaServiceImplTest {

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private VendaServiceImpl vendaService;

    private Venda venda1;
    private Venda venda2;
    private Pageable pageable;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by("criadoEm"));

        dataInicio = LocalDateTime.now().minusDays(7);
        dataFim = LocalDateTime.now();

        venda1 = criarVenda(1L, "João Silva", new BigDecimal("100.00"),
                LocalDateTime.now().minusDays(5), 1L, 1L, TipoVenda.VENDA_DIRETA);

        venda2 = criarVenda(2L, "Maria Santos", new BigDecimal("200.00"),
                LocalDateTime.now().minusDays(3), 2L, 2L, TipoVenda.VENDA_ONLINE);
    }

    @Test
    @DisplayName("Deve buscar vendas com todos os filtros")
    void deveBuscarVendasComTodosFiltros() {
        List<Venda> vendas = Arrays.asList(venda1, venda2);
        Page<Venda> vendasPage = new PageImpl<>(vendas, pageable, vendas.size());

        when(vendaRepository.findAll(
                eq(1L),
                eq("João"),
                eq(1L),
                eq(1L),
                eq(new BigDecimal("50.00")),
                eq(new BigDecimal("150.00")),
                eq(dataInicio),
                eq(dataFim),
                any(Pageable.class)
        )).thenReturn(vendasPage);

        Page<VendaResponse> resultado = vendaService.listarTodos(
                0, 10, "criadoEm",
                1L, "João", 1L, 1L,
                new BigDecimal("50.00"),
                new BigDecimal("150.00"),
                dataInicio, dataFim
        );

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertTrue(resultado.getContent().stream()
                .anyMatch(v -> v.getCliente().getUsuario().getPessoa().getNome().equals("João Silva")));
        assertTrue(resultado.getContent().stream()
                .anyMatch(v -> true));
    }

    @Test
    @DisplayName("Deve buscar vendas apenas por nome do cliente")
    void deveBuscarVendasPorNomeCliente() {
        List<Venda> vendas = Arrays.asList(venda1, venda2);
        Page<Venda> vendasPage = new PageImpl<>(vendas, pageable, vendas.size());

        when(vendaRepository.findAll(
                eq(null),
                eq("João"),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class)
        )).thenReturn(vendasPage);

        Page<VendaResponse> resultado = vendaService.listarTodos(
                0, 10, "criadoEm",
                null, "João", null, null,
                null, null, null, null
        );

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertTrue(resultado.getContent().stream()
                .anyMatch(v -> v.getCliente().getUsuario().getPessoa().getNome().equals("João Silva")));
        assertTrue(resultado.getContent().stream()
                .anyMatch(v -> true));
    }

    private Venda criarVenda(Long id, String nomeCliente, BigDecimal valorTotal,
                             LocalDateTime dataVenda, Long statusId, Long pagamentoId,
                             TipoVenda tipoVenda) {
        Venda venda = new Venda();
        venda.setId(id);
        venda.setValorTotal(valorTotal);
        venda.setDataVenda(dataVenda);
        venda.setTipoVenda(tipoVenda);

        Cliente cliente = new Cliente();
        Usuario usuario = new Usuario();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nomeCliente);
        pessoa.setUsuario(usuario);
        cliente.setPessoa(pessoa);
        venda.setCliente(cliente);

        Status status = new Status();
        status.setId(statusId);
        venda.setStatus(status);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(pagamentoId);
        venda.setPagamento(pagamento);

        ItemVenda item = new ItemVenda();
        item.setId(id);
        item.setQuantidade(1);
        item.setPreco(valorTotal);
        item.setValorTotal(valorTotal);

        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Produto " + id);
        produto.setPreco(valorTotal);

        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome("Categoria " + id);
        produto.setCategoria(categoria);

        item.setProduto(produto);
        item.setVenda(venda);

        venda.setItens(List.of(item));

        return venda;
    }
}