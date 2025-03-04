package br.com.rodrigo.gestortarefas.api.exception;

public enum MensagensError {

    // Geral
    USUARIO_NAO_ENCONTRADO_POR_LOGIN("Usuário não encontrado para o login %s"),
    PERFIL_POSSUI_USUARIO("Perfil não pode ser apagado, está vinculado a um usuário"),
    USUARIO_POSSUI_TAREFA("Usuário não pode ser apagado, está vinculado a uma tarefa"),
    TAREFA_NAO_ENCONTRADA_POR_ID("Tarefa não encontrada para o id %s"),
    SMS_ENVIADO_SUCESSO("SMS enviado com sucesso! ID: %s"),
    ERRO_ENVIO_SMS("Erro ao enviar SMS: %s"),
    EMAIL_JA_CADASTRADO("E-mail %s já cadastrado"),
    CPF_JA_CADASTRADO("CPF %s já cadastrado"),
    USUARIO_NAO_ENCONTRADO_POR_ID("Usuário não encontrado para o id %s"),
    PERFIL_NAO_ENCONTRADO("Perfil não encontrado para o id %s");

    private final String message;

    MensagensError(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
