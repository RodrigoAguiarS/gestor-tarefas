package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.services.GenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;



public abstract class GenericControllerImpl<Form, Response> implements GenericController<Form, Response> {

    protected final GenericService<Form, Response> service;

    protected GenericControllerImpl(GenericService<Form, Response> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Response> cadastrar(@RequestBody Form form) {
        Response response = service.cadastrar(form);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Response> atualizar(@PathVariable Long id, @RequestBody Form form) {
        Response response = service.atualizar(id, form);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Response> obterPorId(@PathVariable Long id) {
        Response response = service.obterPorId(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<Response>> listarTodos(Pageable pageable) {
        Page<Response> responses = service.listarTodos(pageable);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        service.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}