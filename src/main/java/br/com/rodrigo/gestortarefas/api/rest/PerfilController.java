package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.form.PerfilForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/perfis")
@RequiredArgsConstructor
public class PerfilController extends ControllerBase<PerfilResponse> {

    private final IPerfil perfilService;

    @PostMapping
    public ResponseEntity<PerfilResponse> criar(@RequestBody @Valid PerfilForm perfilForm) {
        PerfilResponse response = perfilService.criar(perfilForm);
        return responderItemCriado(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponse> atualizar(@PathVariable @Valid Long id, @RequestBody PerfilForm perfilForm) {
        PerfilResponse response = perfilService.atualizar(id, perfilForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PerfilResponse> deletar(@PathVariable Long id) {
        perfilService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> consultarPorId(@PathVariable Long id) {
        Optional<PerfilResponse> response = perfilService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping
    public ResponseEntity<Page<PerfilResponse>> consultarTodos(Pageable pageable) {
        Page<PerfilResponse> perfilResponses = perfilService.consultarTodos(pageable);
        return responderListaDeItensPaginada(perfilResponses);
    }
}