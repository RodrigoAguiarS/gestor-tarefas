package br.com.rodrigo.gestortarefas.api.services.estoque;

import br.com.rodrigo.gestortarefas.api.model.AcaoMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.OrigemMovimentacao;
import br.com.rodrigo.gestortarefas.api.model.TipoMovimentacao;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MovimentacaoContext {
    private final Object item;
    private final int quantidade;
    private final int quantidadeAnterior;
    private final TipoMovimentacao tipo;
    private final OrigemMovimentacao origem;
    private final AcaoMovimentacao acao;
    private final Long idOrigem;
}
