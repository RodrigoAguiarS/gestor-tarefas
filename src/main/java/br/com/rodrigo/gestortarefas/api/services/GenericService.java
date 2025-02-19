package br.com.rodrigo.gestortarefas.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface GenericService<Form, Response> {

    Response cadastrar(Form form);

    Response atualizar(Long id, Form form);

    Response obterPorId(Long id);

    Page<Response> listarTodos(Pageable pageable);

    void apagar(Long id);

    void ativar(Long id);

    void desativar(Long id);
}
