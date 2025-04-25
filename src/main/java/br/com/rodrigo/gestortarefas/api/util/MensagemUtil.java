package br.com.rodrigo.gestortarefas.api.util;

import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class MensagemUtil {
    private static final String TEMPLATE_TAREFA = """
            📋 Nova Tarefa
            🔑 Código: %d
            📌 Título: %s
            📝 Descrição: %s
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

    private static final String TEMPLATE_VENDA_REALIZADA = """
            🛍️ Nova Venda Realizada
            🔑 Código: #%d
            👤 Cliente: %s
            💰 Valor Total: R$ %.2f
            🏷️ Tipo: %s
            💳 Forma de Pagamento: %s
            📅 Data: %s""";

    private static final String TEMPLATE_MUDANCA_STATUS_VENDA = """
            📦 Atualização de Status
            🔑 Venda #%d
            👤 Cliente: %s
            🔄 Novo Status: %s
            📝 Observação: %s
            📅 Data: %s""";

    public static String criarMensagemVendaRealizada(Venda venda) {
        return String.format(TEMPLATE_VENDA_REALIZADA,
                venda.getId(),
                venda.getCliente().getUsuario().getPessoa().getNome(),
                venda.getValorTotal(),
                venda.getTipoVenda().name(),
                venda.getPagamento().getNome(),
                venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    public static String criarMensagemMudancaStatusVenda(Venda venda, String observacao) {
        return String.format(TEMPLATE_MUDANCA_STATUS_VENDA,
                venda.getId(),
                venda.getCliente().getUsuario().getPessoa().getNome(),
                venda.getStatus().getNome(),
                observacao != null ? observacao : "Sem observações",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }


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
