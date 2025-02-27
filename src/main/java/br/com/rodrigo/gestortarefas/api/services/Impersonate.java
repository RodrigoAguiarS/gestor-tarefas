package br.com.rodrigo.gestortarefas.api.services;

public interface Impersonate {

    String iniciarPersonificacao(String email);

    void encerrarPersonificacao(String email);
}
