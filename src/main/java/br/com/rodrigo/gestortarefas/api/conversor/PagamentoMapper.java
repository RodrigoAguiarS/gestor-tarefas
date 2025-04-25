package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;

public class PagamentoMapper {

    public static Pagamento responseParaEntidade(PagamentoResponse response) {
        if (response == null) {
            return null;
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setId(response.getId());
        pagamento.setNome(response.getNome());
        pagamento.setDescricao(response.getDescricao());
        pagamento.setAtivo(response.getAtivo());
        pagamento.setPorcentagemAcrescimo(response.getPorcentagemAcrescimo());
        return pagamento;
    }

    public static PagamentoResponse entidadeParaResponse(Pagamento pagamento) {
        if (pagamento == null) {
            return null;
        }
        PagamentoResponse response = new PagamentoResponse();
        response.setId(pagamento.getId());
        response.setNome(pagamento.getNome());
        response.setDescricao(pagamento.getDescricao());
        response.setAtivo(pagamento.getAtivo());
        response.setPorcentagemAcrescimo(pagamento.getPorcentagemAcrescimo());
        return response;
    }
}
