package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimentacaoFactory {
    private final ProdutoRepository produtoRepository;

    public MovimentacaoStrategy criarStrategy(Object item) {
        if (item instanceof ItemVenda) {
            return new ItemVendaStrategy(produtoRepository);
        }
        if (item instanceof Produto) {
            return new ProdutoStrategy(produtoRepository);
        }
        throw new IllegalArgumentException("Tipo de item inválido para movimentação de estoque");
    }
}
