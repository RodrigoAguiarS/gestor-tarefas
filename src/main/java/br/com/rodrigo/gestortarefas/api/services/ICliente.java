package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ICliente {
    ClienteResponse criar(ClienteForm clienteForm);
    ClienteResponse atualizar(Long id, ClienteForm clienteForm);
    void deletar(Long id);
    Optional<ClienteResponse> consultarPorId(Long id);
    Optional<ClienteResponse> getClienteLogado();
    Page<ClienteResponse> buscar(int page, int size, String sort, String email,
                                 String nome, String cpf, String cidade, String estado, String cep);
}