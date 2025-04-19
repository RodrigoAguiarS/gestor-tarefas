package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.form.PagamentoForm;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;
import br.com.rodrigo.gestortarefas.api.services.IPagamento;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController extends ControllerBase<PagamentoResponse> {

    private final IPagamento pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoResponse> criar(@RequestBody @Valid PagamentoForm pagamentoForm) {
        PagamentoResponse response = pagamentoService.criar(pagamentoForm);
        return responderItemCriadoComURI(response, ServletUriComponentsBuilder.fromCurrentRequest(), "/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponse> atualizar(@PathVariable @Valid Long id, @RequestBody PagamentoForm pagamentoForm) {
        PagamentoResponse response = pagamentoService.atualizar(id, pagamentoForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoResponse> deletar(@PathVariable Long id) {
        pagamentoService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> consultarPorId(@PathVariable Long id) {
        Optional<PagamentoResponse> response = pagamentoService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping()
    public ResponseEntity<Page<PagamentoResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) Long id,
                                                            @RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String porcentagemAcrescimo,
                                                            @RequestParam(required = false) String descricao) {
        Page<PagamentoResponse> pagamento = pagamentoService.listarTodos(page, size, sort, id, nome, descricao, porcentagemAcrescimo);
        return responderListaDeItensPaginada(pagamento);
    }
}