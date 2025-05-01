package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProdutoStrategy implements MovimentacaoStrategy {
    private final ProdutoRepository produtoRepository;

    @Override
    public ResultadoMovimentacao processar(MovimentacaoContext context) {
        Produto produtoItem = (Produto) context.getItem();

        if (context.getAcao() == AcaoMovimentacao.CRIACAO_PRODUTO) {
            return processarCriacaoProduto(produtoItem, context);
        }

        return processarAtualizacaoProduto(produtoItem, context);
    }

    private ResultadoMovimentacao processarCriacaoProduto(Produto produto, MovimentacaoContext context) {
        produto.setQuantidade(context.getQuantidade());

        return ResultadoMovimentacao.criar(
                produto,
                context.getQuantidade(),
                TipoMovimentacao.ENTRADA,
                context.getOrigem(),
                context.getAcao(),
                context.getIdOrigem(),
                0
        );
    }

    private ResultadoMovimentacao processarAtualizacaoProduto(Produto produtoItem,
                                                              MovimentacaoContext context) {
        Produto produtoAtual = produtoRepository.findById(produtoItem.getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage()));

        int quantidadeAnterior = produtoAtual.getQuantidade();
        int diferenca = context.getQuantidade() - quantidadeAnterior;
        if (diferenca == 0) {
            return null;
        }

        TipoMovimentacao tipo = diferenca > 0 ? TipoMovimentacao.ENTRADA : TipoMovimentacao.SAIDA;
        int quantidadeMovimentacao = Math.abs(diferenca);

        if (tipo == TipoMovimentacao.SAIDA && quantidadeAnterior < quantidadeMovimentacao) {
            throw new ViolacaoIntegridadeDadosException(
                    MensagensError.ESTOQUE_INSUFICIENTE.getMessage(produtoItem.getNome()));
        }

        produtoItem.setQuantidade(context.getQuantidade());

        return ResultadoMovimentacao.criar(
                produtoItem,
                quantidadeMovimentacao,
                tipo,
                context.getOrigem(),
                context.getAcao(),
                context.getIdOrigem(),
                quantidadeAnterior
        );
    }
}