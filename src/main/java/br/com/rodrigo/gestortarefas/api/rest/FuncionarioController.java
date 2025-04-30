package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.form.FuncionarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.FuncionarioResponse;
import br.com.rodrigo.gestortarefas.api.services.IFuncionario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController extends ControllerBase<FuncionarioResponse> {

    private final IFuncionario funcionarioService;

    @PostMapping
    public ResponseEntity<FuncionarioResponse> criar(@RequestBody @Valid FuncionarioForm funcionarioForm, UriComponentsBuilder uriBuilder) {
        FuncionarioResponse response = funcionarioService.criar(null, funcionarioForm);
        return responderItemCriadoComURI(response, uriBuilder, "/funcionarios/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid FuncionarioForm funcionarioForm) {
        FuncionarioResponse response = funcionarioService.criar(id, funcionarioForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponse> consultarPorId(@PathVariable Long id) {
        Optional<FuncionarioResponse> response = funcionarioService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<FuncionarioResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) String email,
                                                            @RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String cpf,
                                                            @RequestParam(required = false) String cargo,
                                                            @RequestParam(required = false) Long perfilId,
                                                            @RequestParam(required = false) String matricula) {
        Page<FuncionarioResponse> funcionarios = funcionarioService.buscar(page, size, sort, email, nome, cpf, cargo, matricula, perfilId);
        return responderListaDeItensPaginada(funcionarios);
    }
}