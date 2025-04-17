package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.CategoriaForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICategoria {
    CategoriaResponse criar(CategoriaForm tarefaForm);
    CategoriaResponse atualizar(Long id, CategoriaForm tarefaForm);
    void deletar(Long id);
    Optional<CategoriaResponse> consultarPorId(Long id);
    Page<CategoriaResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                        String descricao);
}
