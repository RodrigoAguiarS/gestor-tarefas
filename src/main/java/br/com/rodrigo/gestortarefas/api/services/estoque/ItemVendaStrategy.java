package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemVendaStrategy implements MovimentacaoStrategy {
    private final ProdutoRepository produtoRepository;

    @Override
    public ResultadoMovimentacao processar(MovimentacaoContext context) {
        if (context.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        ItemVenda itemVenda = (ItemVenda) context.getItem();
        Produto produto = buscarProduto(itemVenda.getProduto().getId());
        int quantidadeAnterior = produto.getQuantidade();

        validarEstoque(produto, context);
        atualizarEstoque(produto, context);

        return ResultadoMovimentacao.criar(
                produto,
                context.getQuantidade(),
                context.getTipo(),
                context.getOrigem(),
                context.getAcao(),
                context.getIdOrigem(),
                quantidadeAnterior
        );
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage()));
    }

    private void validarEstoque(Produto produto, MovimentacaoContext context) {
        if (context.getTipo() == TipoMovimentacao.SAIDA &&
                produto.getQuantidade() < context.getQuantidade()) {
            throw new ViolacaoIntegridadeDadosException(
                    MensagensError.ESTOQUE_INSUFICIENTE.getMessage(produto.getNome()));
        }
    }

    private void atualizarEstoque(Produto produto, MovimentacaoContext context) {
        int novoEstoque = context.getTipo() == TipoMovimentacao.SAIDA
                ? produto.getQuantidade() - context.getQuantidade()
                : produto.getQuantidade() + context.getQuantidade();
        produto.setQuantidade(novoEstoque);
    }
}