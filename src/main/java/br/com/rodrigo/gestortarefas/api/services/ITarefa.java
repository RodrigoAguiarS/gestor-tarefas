package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.form.TarefaForm;
import br.com.rodrigo.gestortarefas.api.model.response.TarefaResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ITarefa {
    TarefaResponse criar(TarefaForm tarefaForm);
    TarefaResponse atualizar(Long id, TarefaForm tarefaForm);
    void deletar(Long id);
    Optional<TarefaResponse> consultarPorId(Long id);
    Page<TarefaResponse> listarTodos(int page, int size, String sort, Long id, String titulo,
                                     String descricao, Situacao situacao, Prioridade prioridade, Long responsavelId);
}
