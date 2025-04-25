package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.services.ICliente;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController extends ControllerBase<ClienteResponse> {

    private final ICliente clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@RequestBody @Valid ClienteForm clienteForm, UriComponentsBuilder uriBuilder) {
        ClienteResponse response = clienteService.criar(clienteForm);
        return responderItemCriadoComURI(response, uriBuilder, "/clientes/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteForm clienteForm) {
        ClienteResponse response = clienteService.atualizar(id, clienteForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteResponse> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultarPorId(@PathVariable Long id) {
        Optional<ClienteResponse> response = clienteService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ClienteResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) String email,
                                                            @RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String cpf,
                                                            @RequestParam(required = false) String cidade,
                                                            @RequestParam(required = false) String estado,
                                                            @RequestParam(required = false) String cep) {
        Page<ClienteResponse> clientes = clienteService.buscar(page, size, sort, email, nome, cpf, cidade, estado, cep);
        return responderListaDeItensPaginada(clientes);
    }

    @GetMapping("/logado")
    public ResponseEntity<ClienteResponse> getClienteLogado() {
        ClienteResponse clienteResponse = clienteService.getClienteLogado()
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage()));
        return responderSucessoComItem(clienteResponse);
    }
}