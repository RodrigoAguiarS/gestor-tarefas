package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.services.UsuarioServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends GenericControllerImpl<UsuarioForm, UsuarioResponse> {

    private final UsuarioServiceImpl usuarioServiceImpl;

    public UsuarioController(UsuarioServiceImpl usuarioServiceImpl) {
        super(usuarioServiceImpl);
        this.usuarioServiceImpl = usuarioServiceImpl;
    }

    @GetMapping("/papel")
    public ResponseEntity<List<String>> getRoles(Authentication authentication) {
        String email = authentication.getName();
        List<String> roles = usuarioServiceImpl.obterPerfis(email);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<UsuarioResponse>> listarTodos(@RequestParam int page,
                                                             @RequestParam int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String email,
                                                             @RequestParam(required = false) String nome,
                                                             @RequestParam(required = false) String cpf,
                                                             @RequestParam(required = false) Long perfilId) {
        Page<UsuarioResponse> usuarios = usuarioServiceImpl.listarTodos(page, size, sort, email, nome, cpf, perfilId);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/dados")
    public ResponseEntity<Usuario> obterUsuarioPorEmail(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioServiceImpl.obterUsuarioPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
}
