package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import br.com.rodrigo.gestortarefas.api.model.response.ItemVendaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VendaMapper {


    public static VendaResponse entidadeParaResponse(Venda venda) {
        VendaResponse response = new VendaResponse();
        response.setId(venda.getId());
        response.setTipoVenda(venda.getTipoVenda().name());
        response.setCliente(ClienteMapper.entidadeParaResponse(venda.getCliente()));
        response.setDataVenda(venda.getDataVenda());
        response.setPagamento(PagamentoMapper.entidadeParaResponse(venda.getPagamento()));
        response.setValorTotal(venda.getValorTotal());
        response.setStatus(StatusMapper.entidadeParaResponse(venda.getStatus()));
        response.setItens(mapearItensResponse(venda.getItens()));
        return response;
    }

    private static List<ItemVendaResponse> mapearItensResponse(List<ItemVenda> itens) {
        return itens.stream()
                .map(item -> new ItemVendaResponse(
                        item.getProduto().getDescricao(),
                        ProdutoMapper.entidadeParaResponse(item.getProduto()),
                        item.getQuantidade(),
                        item.getPreco(),
                        item.getValorTotal()
                ))
                .collect(Collectors.toList());
    }
}
