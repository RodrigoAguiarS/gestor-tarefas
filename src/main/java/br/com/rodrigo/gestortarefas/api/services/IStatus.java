package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.StatusForm;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IStatus {
    StatusResponse criar(StatusForm statusForm);
    StatusResponse atualizar(Long id, StatusForm statusForm);
    void deletar(Long id);
    Optional<StatusResponse> consultarPorId(Long id);
    List<StatusResponse> buscarPorIds(List<Long> ids);
    Page<StatusResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                        String descricao);
}
