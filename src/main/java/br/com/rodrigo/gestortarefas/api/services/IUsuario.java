package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;

import java.util.List;

public interface IUsuario {
    List<String> obterPerfis(String email);
    UsuarioResponse obterUsuarioLogado();
}
