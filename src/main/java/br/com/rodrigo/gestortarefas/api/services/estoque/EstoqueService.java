package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.repository.MovimentacaoEstoqueRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstoqueService {
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final MovimentacaoFactory movimentacaoFactory;

    public void processarMovimentacao(Object item, int quantidade, TipoMovimentacao tipo,
                                      OrigemMovimentacao origem, AcaoMovimentacao acao, Long idOrigem) {
        MovimentacaoStrategy strategy = movimentacaoFactory.criarStrategy(item);

        Produto produto;
        int quantidadeAnterior = 0;

        if (item instanceof Produto) {
            produto = (Produto) item;
            quantidadeAnterior = produto.getQuantidade();
        } else if (item instanceof ItemVenda) {
            produto = ((ItemVenda) item).getProduto();
            quantidadeAnterior = produto != null ? produto.getQuantidade() : 0;
        }

        MovimentacaoContext context = MovimentacaoContext.builder()
                .item(item)
                .quantidade(quantidade)
                .tipo(tipo)
                .origem(origem)
                .acao(acao)
                .idOrigem(idOrigem)
                .quantidadeAnterior(quantidadeAnterior)
                .build();

        ResultadoMovimentacao resultado = strategy.processar(context);

        if (resultado != null) {
            salvarMovimentacao(resultado);
        }
    }

    private void salvarMovimentacao(ResultadoMovimentacao resultado) {
        produtoRepository.save(resultado.produto());
        movimentacaoRepository.save(resultado.movimentacao());
    }
}
