package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuario {
    UsuarioResponse criar(Long idUsuario, UsuarioForm usuarioForm);
    void deletar(Long id);
    Optional<UsuarioResponse> consultarPorId(Long id);
    Page<UsuarioResponse> buscar(int page, int size, String sort, String email,
                                 String nome, String cpf, Long perfilId);
    Page<UsuarioResponse> listarTodos(Pageable pageable);
    List<String> obterPerfis(String email);
    Usuario obterUsuarioLogado();
}
