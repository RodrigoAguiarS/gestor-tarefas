package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.RegistroEntrada;
import br.com.rodrigo.gestortarefas.api.model.Usuario;



public interface IRegistroEntrada {

    void registrarEntrada(Usuario usuario, String ipAddress);

    RegistroEntrada obterUltimoRegistroEntrada();
}
