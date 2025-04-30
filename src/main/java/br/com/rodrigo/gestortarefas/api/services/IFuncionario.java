package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.FuncionarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.FuncionarioResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IFuncionario {
    FuncionarioResponse criar(Long idCliente, FuncionarioForm funcionarioForm);
    void deletar(Long id);
    Optional<FuncionarioResponse> consultarPorId(Long idCliente);
    Page<FuncionarioResponse> buscar(int page, int size, String sort, String email,
                                 String nome, String cpf, String cargo, String matricula, Long perfilId);
}
