package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;

public class StatusMapper {

    public static Status responseParaEntidade(StatusResponse response) {
        if (response == null) {
            return null;
        }

        Status status = new Status();
        status.setId(response.getId());
        status.setNome(response.getNome());
        status.setDescricao(response.getDescricao());
        return status;
    }

    public static StatusResponse entidadeParaResponse(Status status) {
        if (status == null) {
            return null;
        }

        StatusResponse response = new StatusResponse();
        response.setId(status.getId());
        response.setNome(status.getNome());
        response.setDescricao(status.getDescricao());
        return response;
    }
}