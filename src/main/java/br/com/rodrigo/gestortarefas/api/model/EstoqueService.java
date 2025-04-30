package br.com.rodrigo.gestortarefas.api.model;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.repository.MovimentacaoEstoqueRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;


    public void processarMovimentacao(ItemVenda item, TipoMovimentacao tipo, OrigemMovimentacao origem,
                                      AcaoMovimentacao acao, Long idOrigem) {
        Produto produto = produtoRepository.findById(item.getProduto().getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage()));

        int quantidade = item.getQuantidade();

        if (tipo == TipoMovimentacao.SAIDA && produto.getQuantidade() < quantidade) {
            throw new ViolacaoIntegridadeDadosException(MensagensError.ESTOQUE_INSUFICIENTE.getMessage(produto.getNome()));
        }

        int novoEstoque = tipo == TipoMovimentacao.SAIDA
                ? produto.getQuantidade() - quantidade
                : produto.getQuantidade() + quantidade;

        produto.setQuantidade(novoEstoque);
        produtoRepository.save(produto);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setQuantidade(quantidade);
        mov.setTipo(tipo);
        mov.setOrigem(origem);
        mov.setAcao(acao);
        mov.setIdOrigem(idOrigem);
        movimentacaoRepository.save(mov);
    }

    public void processarMovimentacaoProduto(Produto produto, int quantidade, TipoMovimentacao tipo,
                                             OrigemMovimentacao origem, AcaoMovimentacao acao, Long idOrigem) {
        if (acao == AcaoMovimentacao.CRIACAO_PRODUTO) {
            produto.setQuantidade(quantidade);
        } else {
            if (tipo == TipoMovimentacao.SAIDA && produto.getQuantidade() < quantidade) {
                throw new ViolacaoIntegridadeDadosException(MensagensError.ESTOQUE_INSUFICIENTE.getMessage(produto.getNome()));
            }

            int novoEstoque = tipo == TipoMovimentacao.SAIDA
                    ? produto.getQuantidade() - quantidade
                    : produto.getQuantidade() + quantidade;

            produto.setQuantidade(novoEstoque);
        }

        produtoRepository.save(produto);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setQuantidade(quantidade);
        movimentacao.setTipo(tipo);
        movimentacao.setOrigem(origem);
        movimentacao.setAcao(acao);
        movimentacao.setIdOrigem(idOrigem);
        movimentacaoRepository.save(movimentacao);
    }
}
