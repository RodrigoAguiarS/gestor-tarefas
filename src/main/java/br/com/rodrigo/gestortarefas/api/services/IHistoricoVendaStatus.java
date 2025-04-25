package br.com.rodrigo.gestortarefas.api.services;


import br.com.rodrigo.gestortarefas.api.model.response.HistoricoStatusVendaResponse;

import java.util.List;

public interface IHistoricoVendaStatus {

    List<HistoricoStatusVendaResponse> buscarHistoricoStatus(Long vendaId);

}
