package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.TarefaResponse;
import br.com.rodrigo.gestortarefas.api.repository.TarefaRepository;
import br.com.rodrigo.gestortarefas.api.services.impl.TarefaServiceImpl;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceImplTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaServiceImpl tarefaService;

    private Tarefa tarefa;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        new ModelMapperUtil(modelMapper);

        tarefa = criarTarefa();
    }

    @Test
    void deveListarTarefasPorResponsavel() {
        when(tarefaRepository.findAllByResponsavelId(anyLong())).thenReturn(Collections.singletonList(tarefa));

        List<TarefaResponse> responses = tarefaService.listarTarefasPorResponsavel(tarefa.getResponsavel().getId());

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(tarefa.getTitulo(), responses.get(0).getTitulo());
    }

    @Test
    void deveConcluirTarefa() {
        when(tarefaRepository.findById(anyLong())).thenReturn(Optional.of(tarefa));

        tarefaService.concluirTarefa(tarefa.getId());

        assertEquals(Situacao.CONCLUIDA, tarefa.getSituacao());
        verify(tarefaRepository, times(1)).save(tarefa);
    }

    @Test
    void deveListarTodos() {
        Page<Tarefa> page = new PageImpl<>(Collections.singletonList(tarefa));
        when(tarefaRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
        Page<TarefaResponse> responses = tarefaService.listarTodos(0, 10, "id", null, null, null, null, null, null);
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(tarefa.getTitulo(), responses.getContent().get(0).getTitulo());
    }

    private Tarefa criarTarefa() {
        Usuario responsavel = new Usuario();
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        responsavel.setId(1L);
        responsavel.setPessoa(pessoa);
        responsavel.getPessoa().setNome("Rodrigo Teste");
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Título das tarefas");
        tarefa.setDescricao("Descrição da Tarefa");
        tarefa.setSituacao(Situacao.PENDENTE);
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setResponsavel(responsavel);

        return tarefa;
    }
}