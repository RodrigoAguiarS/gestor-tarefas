package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.form.StatusForm;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.repository.StatusRepository;
import br.com.rodrigo.gestortarefas.api.services.impl.StatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusServiceImplTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusServiceImpl statusService;

    private StatusForm statusForm;
    private Status status;

    @BeforeEach
    void setUp() {
        statusForm = new StatusForm();
        statusForm.setNome("Status Teste");
        statusForm.setDescricao("Descrição Teste");

        status = new Status();
        status.setId(1L);
        status.setNome("Status Teste");
        status.setDescricao("Descrição Teste");

        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setId(1L);
        statusResponse.setNome("Status Teste");
        statusResponse.setDescricao("Descrição Teste");
    }

    @Test
    void criar_DeveRetornarStatusCriada() {
        when(statusRepository.save(any(Status.class))).thenReturn(status);

        StatusResponse resultado = statusService.criar(statusForm);

        assertNotNull(resultado);
        assertEquals(status.getId(), resultado.getId());
        assertEquals(status.getNome(), resultado.getNome());
        assertEquals(status.getDescricao(), resultado.getDescricao());
        verify(statusRepository, times(1)).save(any(Status.class));
    }

    @Test
    void atualizar_DeveRetornarStatusAtualizada() {
        Long id = 1L;
        when(statusRepository.save(any(Status.class))).thenReturn(status);

        StatusResponse resultado = statusService.atualizar(id, statusForm);

        assertNotNull(resultado);
        assertEquals(status.getId(), resultado.getId());
        assertEquals(status.getNome(), resultado.getNome());
        assertEquals(status.getDescricao(), resultado.getDescricao());
        verify(statusRepository, times(1)).save(any(Status.class));
    }

    @Test
    void consultarPorId_QuandoStatusExiste_DeveRetornarStatus() {
        Long id = 1L;
        when(statusRepository.findById(id)).thenReturn(Optional.of(status));

        Optional<StatusResponse> resultado = statusService.consultarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(status.getId(), resultado.get().getId());
        assertEquals(status.getNome(), resultado.get().getNome());
        assertEquals(status.getDescricao(), resultado.get().getDescricao());
    }

    @Test
    void consultarPorId_QuandoStatusNaoExiste_DeveRetornarVazio() {
        Long id = 1L;
        when(statusRepository.findById(id)).thenReturn(Optional.empty());

        Optional<StatusResponse> resultado = statusService.consultarPorId(id);

        assertFalse(resultado.isPresent());
    }

    @Test
    void listarTodos_DeveRetornarPaginaComStatuss() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Status> statussPage = new PageImpl<>(
                Collections.singletonList(status),
                pageable,
                1
        );
        doReturn(statussPage).when(statusRepository).findAll(
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
        );

        Page<StatusResponse> resultado = statusService.listarTodos(0, 10, "id",
                null, null, null);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(status.getId(), resultado.getContent().get(0).getId());
        assertEquals(status.getNome(), resultado.getContent().get(0).getNome());
        assertEquals(status.getDescricao(), resultado.getContent().get(0).getDescricao());
    }
}