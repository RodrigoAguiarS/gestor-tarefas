package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.form.StatusForm;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import br.com.rodrigo.gestortarefas.api.services.IStatus;
import br.com.rodrigo.gestortarefas.api.services.IVenda;
import br.com.rodrigo.gestortarefas.api.services.StatusVendaService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController extends ControllerBase<StatusResponse> {

    private final IStatus statusService;
    private final IVenda vendaService;
    private final StatusVendaService statusVendaService;

    @PostMapping
    public ResponseEntity<StatusResponse> criar(@RequestBody @Valid StatusForm statusForm) {
        StatusResponse response = statusService.criar(statusForm);
        return responderItemCriadoComURI(response, ServletUriComponentsBuilder.fromCurrentRequest(), "/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponse> atualizar(@PathVariable @Valid Long id, @RequestBody StatusForm statusForm) {
        StatusResponse response = statusService.atualizar(id, statusForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StatusResponse> deletar(@PathVariable Long id) {
        statusService.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusResponse> consultarPorId(@PathVariable Long id) {
        Optional<StatusResponse> response = statusService.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
    }

    @GetMapping()
    public ResponseEntity<Page<StatusResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) Long id,
                                                            @RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String descricao) {
        Page<StatusResponse> status = statusService.listarTodos(page, size, sort, id, nome, descricao);
        return responderListaDeItensPaginada(status);
    }

    @GetMapping("/{id}/proximos-status")
    public ResponseEntity<List<StatusResponse>> getProximosStatus(
            @PathVariable Long id,
            @RequestParam TipoVenda tipoVenda) {

        VendaResponse venda = vendaService.consultarPorId(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.VENDA_NAO_ENCONTRADA.getMessage(id)));

        List<StatusResponse> proximosStatus = statusVendaService.getProximosStatusPossiveis(
                venda.getStatus().getId(),
                tipoVenda
        );

        return responderListaDeItens(proximosStatus);
    }
}