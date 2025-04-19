package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.PagamentoForm;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPagamento {
    PagamentoResponse criar(PagamentoForm tarefaForm);
    PagamentoResponse atualizar(Long id, PagamentoForm tarefaForm);
    void deletar(Long id);
    Optional<PagamentoResponse> consultarPorId(Long id);
    Page<PagamentoResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                        String descricao, String porcentagemAcrescimo);
}
