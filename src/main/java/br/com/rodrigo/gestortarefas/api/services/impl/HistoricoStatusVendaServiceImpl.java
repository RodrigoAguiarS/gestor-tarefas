package br.com.rodrigo.gestortarefas.api.services.impl;


import br.com.rodrigo.gestortarefas.api.conversor.StatusMapper;
import br.com.rodrigo.gestortarefas.api.model.HistoricoStatusVenda;
import br.com.rodrigo.gestortarefas.api.model.response.HistoricoStatusVendaResponse;
import br.com.rodrigo.gestortarefas.api.repository.HistoricoStatusVendaRepository;
import br.com.rodrigo.gestortarefas.api.services.IHistoricoVendaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HistoricoStatusVendaServiceImpl implements IHistoricoVendaStatus {

    private final HistoricoStatusVendaRepository historicoStatusVendaRepository;

    @Override
    public List<HistoricoStatusVendaResponse> buscarHistoricoStatus(Long vendaId) {
        return historicoStatusVendaRepository.findByVendaIdOrderByCriadoEmAsc(vendaId)
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    private HistoricoStatusVendaResponse converterParaResponse(HistoricoStatusVenda historico) {
        return HistoricoStatusVendaResponse.builder()
                .id(historico.getId())
                .status(StatusMapper.entidadeParaResponse(historico.getStatus()))
                .criadoEm(historico.getCriadoEm())
                .atualizadoEm(historico.getAtualizadoEm())
                .observacao(historico.getObservacao())
                .build();
    }
}
