package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController extends ControllerBase<UsuarioResponse> {

    private final IUsuario usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@RequestBody @Valid UsuarioForm usuarioForm) {
        UsuarioResponse response = usuarioService.criar(usuarioForm);
        return responderItemCriadoComURI(response, ServletUriComponentsBuilder.fromCurrentRequest(), "/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioForm usuarioForm) {
        UsuarioResponse response = usuarioService.atualizar(id, usuarioForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> consultarPorId(@PathVariable Long id) {
        Optional<UsuarioResponse> response = usuarioService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> listarTodos(Pageable pageable) {
        Page<UsuarioResponse> responses = usuarioService.listarTodos(pageable);
        return responderListaDeItensPaginada(responses);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<UsuarioResponse>> buscar(@RequestParam int page,
                                                        @RequestParam int size,
                                                        @RequestParam(required = false) String sort,
                                                        @RequestParam(required = false) String email,
                                                        @RequestParam(required = false) String nome,
                                                        @RequestParam(required = false) String cpf,
                                                        @RequestParam(required = false) Long perfilId) {
        Page<UsuarioResponse> usuarios = usuarioService.buscar(page, size, sort, email, nome, cpf, perfilId);
        return responderListaDeItensPaginada(usuarios);
    }

    @GetMapping("/papel")
    public ResponseEntity<List<String>> getRoles(Authentication authentication) {
        String email = authentication.getName();
        List<String> roles = usuarioService.obterPerfis(email);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/logado")
    public ResponseEntity<Usuario> obterUsuarioLogado() {
        Usuario usuarioResponse = usuarioService.obterUsuarioLogado();
        return ResponseEntity.ok(usuarioResponse);
    }
}