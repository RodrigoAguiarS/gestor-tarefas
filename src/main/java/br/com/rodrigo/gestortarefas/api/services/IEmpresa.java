package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.EmpresaForm;
import br.com.rodrigo.gestortarefas.api.model.response.EmpresaResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IEmpresa {
    EmpresaResponse criar(EmpresaForm empresaForm, Long id);
    void deletar(Long id);
    Optional<EmpresaResponse> consultarPorId(Long id);
    Page<EmpresaResponse> listarTodos(int page, int size, String sort, String nome, String cnpj, String telefone);
}
