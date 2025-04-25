package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import br.com.rodrigo.gestortarefas.api.model.form.ItemVendaForm;
import br.com.rodrigo.gestortarefas.api.model.form.VendaForm;
import br.com.rodrigo.gestortarefas.api.model.response.ItemVendaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VendaMapper {

    public static Venda formParaEntidade(VendaForm form, Cliente cliente, List<Produto> produtos, Pagamento pagamento) {
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setPagamento(pagamento);
        venda.setDataVenda(LocalDateTime.now());
        venda.setValorTotal(produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        venda.setItens(mapearItens(form.getItens(), produtos, venda));
        return venda;
    }

    private static List<ItemVenda> mapearItens(List<ItemVendaForm> itensForm, List<Produto> produtos, Venda venda) {
        return itensForm.stream()
                .map(itemForm -> {
                    ItemVenda item = new ItemVenda();
                    item.setVenda(venda);
                    item.setProduto(produtos.stream()
                            .filter(p -> p.getId().equals(itemForm.getProduto().getId()))
                            .findFirst()
                            .orElseThrow());
                    item.setQuantidade(itemForm.getQuantidade());
                    return item;
                })
                .collect(Collectors.toList());
    }

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
                        item.getValorTotal(),
                        item.getPreco()
                ))
                .collect(Collectors.toList());
    }
}
