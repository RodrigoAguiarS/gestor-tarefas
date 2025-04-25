package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusVendaService {

    private final IStatus statusService;

    public List<StatusResponse> getProximosStatusPossiveis(Long statusAtualId, TipoVenda tipoVenda) {
        List<Long> proximosStatus = obterProximosStatusIds(statusAtualId, tipoVenda);
        return statusService.buscarPorIds(proximosStatus);
    }

    private List<Long> obterProximosStatusIds(Long statusAtualId, TipoVenda tipoVenda) {
        return switch (tipoVenda) {
            case VENDA_ONLINE -> getStatusVendaOnline(statusAtualId);
            case VENDA_DIRETA -> getStatusVendaDireta(statusAtualId);
            case VENDA_EXTERNA -> getStatusVendaExterna(statusAtualId);
        };
    }

    private List<Long> getStatusVendaOnline(Long statusAtualId) {
        return switch (statusAtualId.intValue()) {
            case 3 -> List.of(Status.EM_ADAMENTO, Status.CANCELADO); // PENDENTE -> EM_ANDAMENTO ou CANCELADO
            case 1 -> List.of(Status.EM_PREPARACAO, Status.CANCELADO); // EM_ANDAMENTO -> EM_PREPARACAO ou CANCELADO
            case 6 -> List.of(Status.EM_TRANSPORTE, Status.CANCELADO); // EM_PREPARACAO -> EM_TRANSPORTE ou CANCELADO
            case 9 -> List.of(Status.ENVIADO, Status.CANCELADO); // EM_TRANSPORTE -> ENVIADO ou CANCELADO
            case 7 -> List.of(Status.ENTREGUE, Status.CANCELADO); // ENVIADO -> ENTREGUE ou CANCELADO
            case 8 -> List.of(Status.CONCLUIDO); // ENTREGUE -> CONCLUIDO
            default -> List.of();
        };
    }

    private List<Long> getStatusVendaDireta(Long statusAtualId) {
        return switch (statusAtualId.intValue()) {
            case 3 -> List.of(Status.EM_ADAMENTO, Status.CANCELADO); // PENDENTE -> EM_ANDAMENTO ou CANCELADO
            case 1 -> List.of(Status.EM_PREPARACAO, Status.CANCELADO); // EM_ANDAMENTO -> EM_PREPARACAO ou CANCELADO
            case 6 -> List.of(Status.CONCLUIDO, Status.CANCELADO); // EM_PREPARACAO -> CONCLUIDO ou CANCELADO
            default -> List.of();
        };
    }

    private List<Long> getStatusVendaExterna(Long statusAtualId) {
        return switch (statusAtualId.intValue()) {
            case 3 -> List.of(Status.EM_ADAMENTO, Status.CANCELADO); // PENDENTE -> EM_ANDAMENTO ou CANCELADO
            case 1 -> List.of(Status.EM_PREPARACAO, Status.CANCELADO); // EM_ANDAMENTO -> EM_PREPARACAO ou CANCELADO
            case 6 -> List.of(Status.EM_TRANSPORTE, Status.CANCELADO); // EM_PREPARACAO -> EM_TRANSPORTE ou CANCELADO
            case 9 -> List.of(Status.CONCLUIDO, Status.CANCELADO); // EM_TRANSPORTE -> CONCLUIDO ou CANCELADO
            default -> List.of();
        };
    }
}
