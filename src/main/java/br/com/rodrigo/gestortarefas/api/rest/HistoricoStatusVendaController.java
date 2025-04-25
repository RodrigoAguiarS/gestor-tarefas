package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.response.HistoricoStatusVendaResponse;
import br.com.rodrigo.gestortarefas.api.services.IHistoricoVendaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historico-venda")
public class HistoricoStatusVendaController extends ControllerBase<HistoricoStatusVendaResponse> {

    private final IHistoricoVendaStatus historicoStatusVendaService;

    @GetMapping("/{id}/historico-status")
    public ResponseEntity<List<HistoricoStatusVendaResponse>> buscarHistoricoStatus(
            @PathVariable Long id) {
        return responderListaDeItens(historicoStatusVendaService.buscarHistoricoStatus(id));
    }
}
