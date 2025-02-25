package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.PerfilForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IPerfil {
    PerfilResponse criar(PerfilForm perfilForm);
    PerfilResponse atualizar(Long id, PerfilForm perfilForm);
    void deletar(Long id);
    Optional<PerfilResponse> consultarPorId(Long id);
    Page<PerfilResponse> consultarTodos(Pageable pageable);
}
