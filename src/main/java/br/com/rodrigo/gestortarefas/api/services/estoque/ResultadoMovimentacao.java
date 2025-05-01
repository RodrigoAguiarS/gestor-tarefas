package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.MovimentacaoEstoque;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;

import java.time.LocalDateTime;

public record ResultadoMovimentacao(Produto produto, MovimentacaoEstoque movimentacao) {
    public static ResultadoMovimentacao criar(Produto produto, int quantidade,
                                              TipoMovimentacao tipo, OrigemMovimentacao origem,
                                              AcaoMovimentacao acao, Long idOrigem,
                                              Integer quantidadeAnterior) {
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .quantidade(quantidade)
                .produto(produto)
                .quantidadeAnterior(quantidadeAnterior)
                .tipo(tipo)
                .origem(origem)
                .dataHora(LocalDateTime.now())
                .acao(acao)
                .idOrigem(idOrigem)
                .build();

        return new ResultadoMovimentacao(produto, movimentacao);
    }
}