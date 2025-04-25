package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import java.util.List;


public interface IItemPedido {

    void processarItensPedido(List<ItemVenda> itens, Venda venda);
}
