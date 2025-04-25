package br.com.rodrigo.gestortarefas.api.conversor;


import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import br.com.rodrigo.gestortarefas.api.model.form.ItemVendaForm;
import br.com.rodrigo.gestortarefas.api.model.response.ItemVendaResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ItemVendaMapper {

    public static ItemVenda formParaEntidade(ItemVendaForm form, Venda venda, Produto produto) {
        if (form == null) {
            return null;
        }

        ItemVenda item = new ItemVenda();
        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(form.getQuantidade());
        item.setPreco(produto.getPreco());
        item.calcularValorTotal();
        return item;
    }

    public static List<ItemVenda> formParaEntidade(List<ItemVendaForm> forms, Venda venda, List<Produto> produtos) {
        if (forms == null) {
            return null;
        }

        return forms.stream()
                .map(form -> {
                    Produto produto = produtos.stream()
                            .filter(p -> p.getId().equals(form.getProduto().getId()))
                            .findFirst()
                            .orElseThrow();
                    return formParaEntidade(form, venda, produto);
                })
                .collect(Collectors.toList());
    }

    public static ItemVendaResponse entidadeParaResponse(ItemVenda item) {
        if (item == null) {
            return null;
        }

        return new ItemVendaResponse(
                item.getProduto().getDescricao(),
                ProdutoMapper.entidadeParaResponse(item.getProduto()),
                item.getQuantidade(),
                item.getValorTotal(),
                item.getPreco()
        );
    }

    public static List<ItemVendaResponse> entidadeParaResponse(List<ItemVenda> itens) {
        if (itens == null) {
            return null;
        }

        return itens.stream()
                .map(ItemVendaMapper::entidadeParaResponse)
                .collect(Collectors.toList());
    }
}
