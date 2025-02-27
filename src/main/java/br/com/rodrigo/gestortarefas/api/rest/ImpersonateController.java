package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.form.FormImpersonate;
import br.com.rodrigo.gestortarefas.api.services.Impersonate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/impersonate")
@RequiredArgsConstructor
public class ImpersonateController extends ControllerBase<String>{

    private final Impersonate impersonateService;

    @PostMapping("/logarComo")
    public ResponseEntity<String> iniciarPersonificacao(@Valid @RequestBody FormImpersonate request) {
        String token = impersonateService.iniciarPersonificacao(request.getEmail());
        return responderItemCriado(token);
    }

    @PostMapping("/voltarAoUsuarioLogado")
    public ResponseEntity<String> encerrarPersonificacao(Authentication authentication) {
        impersonateService.encerrarPersonificacao(authentication.getName());
        return responderSucesso();
    }
}