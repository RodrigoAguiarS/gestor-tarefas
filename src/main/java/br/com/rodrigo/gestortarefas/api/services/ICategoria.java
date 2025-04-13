package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.CategoriaForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ICategoria {
    CategoriaResponse criar(CategoriaForm tarefaForm);
    CategoriaResponse atualizar(Long id, CategoriaForm tarefaForm);
    void deletar(Long id);
    Optional<CategoriaResponse> consultarPorId(Long id);
    Page<CategoriaResponse> listarTodos(Pageable pageable);
}
