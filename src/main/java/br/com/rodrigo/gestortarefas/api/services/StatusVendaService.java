package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.StatusTransicao;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.repository.StatusTransicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusVendaService {
    private final IStatus statusService;
    private final StatusTransicaoRepository statusTransicaoRepository;

    public List<StatusResponse> getProximosStatusPossiveis(Long statusAtualId, TipoVenda tipoVenda) {
        List<StatusTransicao> transicoes = statusTransicaoRepository
                .findByStatusAtualIdAndTipoVenda(statusAtualId, tipoVenda);

        List<Long> proximosStatusIds = transicoes.stream()
                .map(transicao -> transicao.getProximoStatus().getId())
                .collect(Collectors.toList());

        return statusService.buscarPorIds(proximosStatusIds);
    }
}