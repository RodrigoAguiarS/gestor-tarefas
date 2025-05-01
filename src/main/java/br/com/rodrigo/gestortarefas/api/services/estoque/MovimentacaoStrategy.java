package br.com.rodrigo.gestortarefas.api.services.estoque;

public interface MovimentacaoStrategy {
    ResultadoMovimentacao processar(MovimentacaoContext context);
}
