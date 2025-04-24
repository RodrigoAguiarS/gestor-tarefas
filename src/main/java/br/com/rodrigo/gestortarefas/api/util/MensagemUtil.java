package br.com.rodrigo.gestortarefas.api.util;

import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class MensagemUtil {
    private static final String TEMPLATE_TAREFA = """
            📋 Nova Tarefa
            🔑 Código: %d
            📌 Título:
            %s
            📝 Descrição:
            %s
            ⚡ Prioridade: %s
            📅 Prazo: %s
            👤 Responsável: %s""";

    private static final String TEMPLATE_MUDANCA_RESPONSAVEL = """
            🔄 Alteração de Responsável
            🔑 Código: %d
            📌 Título: %s
            👤 Novo Responsável: %s""";

    private static final String TEMPLATE_TAREFA_CONCLUIDA = """
            ✅ Tarefa Concluída
            🔑 Código: %d
            📌 Título: %s
            👤 Responsável: %s""";


    public static String criarMensagemNovaTarefa(Tarefa tarefa) {
        return String.format(TEMPLATE_TAREFA,
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getPrioridade(),
                tarefa.getDeadline().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                tarefa.getResponsavel().getPessoa().getNome());
    }

    public static String criarMensagemMudancaResponsavel(Tarefa tarefa) {
        return String.format(TEMPLATE_MUDANCA_RESPONSAVEL,
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getResponsavel().getPessoa().getNome());
    }

    public static String criarMensagemTarefaConcluida(Tarefa tarefa) {
        return String.format(TEMPLATE_TAREFA_CONCLUIDA,
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getResponsavel().getPessoa().getNome());
    }
}
