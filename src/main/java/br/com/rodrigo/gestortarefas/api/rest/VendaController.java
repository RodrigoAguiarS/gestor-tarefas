package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.form.VendaForm;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import br.com.rodrigo.gestortarefas.api.services.IVenda;
import br.com.rodrigo.gestortarefas.api.services.StatusVendaService;
import br.com.rodrigo.gestortarefas.api.util.DateConverterUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController extends ControllerBase<VendaResponse> {

    private final IVenda vendaService;
    private final StatusVendaService statusVendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> criar(@RequestBody @Valid VendaForm vendaForm, UriComponentsBuilder uriBuilder) {
        VendaResponse response = vendaService.criar(vendaForm, null);
        return responderItemCriadoComURI(response, uriBuilder, "/tarefas/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid VendaForm vendaForm) {
        VendaResponse response = vendaService.criar(vendaForm, id);
        return responderSucessoComItem(response);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<VendaResponse>> listarTodos(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) Long status,
            @RequestParam(required = false) Long formaPagamento,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {

        LocalDateTime inicio = DateConverterUtil.parse(dataInicio);
        LocalDateTime fim = DateConverterUtil.parse(dataFim);

        return responderListaDeItensPaginada(
                vendaService.listarTodos(page, size, sort, id, nomeCliente, status,
                        formaPagamento, valorMinimo, valorMaximo, inicio, fim));
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

        return ResponseEntity.ok(proximosStatus);
    }

    @PutMapping("/{id}/status/{statusId}")
    public ResponseEntity<VendaResponse> atualizarStatus(
            @PathVariable Long id,
            @PathVariable Long statusId,
            @RequestParam TipoVenda tipoVenda) {

        VendaResponse venda = vendaService.consultarPorId(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.VENDA_NAO_ENCONTRADA.getMessage(id)));

        List<StatusResponse> statusPossiveis = statusVendaService.getProximosStatusPossiveis(
                venda.getStatus().getId(),
                tipoVenda
        );

        if (statusPossiveis.stream().noneMatch(s -> s.getId().equals(statusId))) {
            throw new IllegalStateException("Status inválido para a operação");
        }

        return ResponseEntity.ok(vendaService.atualizarStatus(id, statusId));
    }
}