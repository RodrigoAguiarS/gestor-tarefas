package br.com.rodrigo.gestortarefas.api.exception;

public class RepositorioNaoInicializadoException extends RuntimeException {
    public RepositorioNaoInicializadoException(String mensagem) {super(mensagem); }
}
