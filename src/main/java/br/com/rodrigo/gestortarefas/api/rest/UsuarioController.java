package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController extends ControllerBase<UsuarioResponse> {

    private final IUsuario usuarioService;

    @GetMapping("/papel")
    public ResponseEntity<List<String>> getRoles(Authentication authentication) {
        String email = authentication.getName();
        List<String> roles = usuarioService.obterPerfis(email);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/logado")
    public ResponseEntity<UsuarioResponse> obterUsuarioLogado() {
        UsuarioResponse usuarioResponse = usuarioService.obterUsuarioLogado();
        return ResponseEntity.ok(usuarioResponse);
    }
}