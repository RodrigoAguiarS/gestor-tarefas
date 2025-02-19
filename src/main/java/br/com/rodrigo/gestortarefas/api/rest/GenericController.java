package br.com.rodrigo.gestortarefas.api.rest;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GenericController<Form, Response> {
    @PostMapping
    ResponseEntity<Response> cadastrar(@Valid @RequestBody Form form);

    @PutMapping("/{id}")
    ResponseEntity<Response> atualizar(@Valid @PathVariable Long id, @RequestBody Form form);

    @GetMapping("/{id}")
    ResponseEntity<Response> obterPorId(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<Response>> listarTodos(Pageable pageable);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> apagar(@PathVariable Long id);

    @PutMapping("/{id}/ativar")
    ResponseEntity<Void> ativar(@PathVariable Long id);

    @PutMapping("/{id}/desativar")
    ResponseEntity<Void> desativar(@PathVariable Long id);
}
