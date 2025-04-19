package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import br.com.rodrigo.gestortarefas.api.model.form.PagamentoForm;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;
import br.com.rodrigo.gestortarefas.api.repository.PagamentoRepository;
import br.com.rodrigo.gestortarefas.api.services.impl.PagamentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceImplTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoServiceImpl pagamentoService;

    private Pagamento pagamento;
    private PagamentoForm pagamentoForm;

    @BeforeEach
    void setUp() {
        pagamento = new Pagamento();
        pagamento.setId(1L);
        pagamento.setNome("Cartão de Crédito");
        pagamento.setDescricao("Pagamento via cartão");
        pagamento.setPorcentagemAcrescimo(new BigDecimal("5.0"));

        pagamentoForm = new PagamentoForm();
        pagamentoForm.setNome("Cartão de Crédito");
        pagamentoForm.setDescricao("Pagamento via cartão");
        pagamentoForm.setPorcentagemAcrescimo(new BigDecimal("5.0"));

        PagamentoResponse pagamentoResponse = new PagamentoResponse();
        pagamentoResponse.setId(1L);
        pagamentoResponse.setNome("Cartão de Crédito");
        pagamentoResponse.setDescricao("Pagamento via cartão");
        pagamentoResponse.setPorcentagemAcrescimo(new BigDecimal("5.0"));
    }

    @Test
    void criar_DeveRetornarPagamentoCriado() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        PagamentoResponse resultado = pagamentoService.criar(pagamentoForm);

        assertNotNull(resultado);
        assertEquals(pagamento.getId(), resultado.getId());
        assertEquals(pagamento.getNome(), resultado.getNome());
        assertEquals(pagamento.getDescricao(), resultado.getDescricao());
        assertEquals(pagamento.getPorcentagemAcrescimo(), resultado.getPorcentagemAcrescimo());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void atualizar_DeveRetornarPagamentoAtualizado() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        PagamentoResponse resultado = pagamentoService.atualizar(1L, pagamentoForm);

        assertNotNull(resultado);
        assertEquals(pagamento.getId(), resultado.getId());
        assertEquals(pagamento.getNome(), resultado.getNome());
        assertEquals(pagamento.getDescricao(), resultado.getDescricao());
        assertEquals(pagamento.getPorcentagemAcrescimo(), resultado.getPorcentagemAcrescimo());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void deletar_DeveExcluirPagamento() {
        doNothing().when(pagamentoRepository).deleteById(any());

        pagamentoService.deletar(1L);

        verify(pagamentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void consultarPorId_DeveRetornarPagamento() {
        when(pagamentoRepository.findById(anyLong())).thenReturn(Optional.of(pagamento));

        Optional<PagamentoResponse> resultado = pagamentoService.consultarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(pagamento.getId(), resultado.get().getId());
        assertEquals(pagamento.getNome(), resultado.get().getNome());
        assertEquals(pagamento.getDescricao(), resultado.get().getDescricao());
        assertEquals(pagamento.getPorcentagemAcrescimo(), resultado.get().getPorcentagemAcrescimo());
    }

    @Test
    void consultarPorId_QuandoNaoEncontrado_DeveRetornarVazio() {
        when(pagamentoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PagamentoResponse> resultado = pagamentoService.consultarPorId(1L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void listarTodos_DeveRetornarPaginaDePagamentos() {
        Page<Pagamento> pagamentoPage = new PageImpl<>(List.of(pagamento));

        when(pagamentoRepository.findAll(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(pagamentoPage);

        Page<PagamentoResponse> resultado = pagamentoService.listarTodos(
                0, 10, "id", 1L, "Cartão", "descrição", "5.0");

        assertNotNull(resultado);
        assertFalse(resultado.getContent().isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(pagamento.getId(), resultado.getContent().get(0).getId());
        assertEquals(pagamento.getNome(), resultado.getContent().get(0).getNome());
    }

    @Test
    void listarTodos_QuandoNaoEncontrado_DeveRetornarPaginaVazia() {
        Page<Pagamento> pagamentoPage = Page.empty();

        when(pagamentoRepository.findAll(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(pagamentoPage);

        Page<PagamentoResponse> resultado = pagamentoService.listarTodos(
                0, 10, "id", null, null, null, null);

        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
    }
}
