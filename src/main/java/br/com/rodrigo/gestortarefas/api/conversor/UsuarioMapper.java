package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;

import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioMapper {

    public static UsuarioResponse entidadeParaResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setPessoa(PessoaMapper.entidadeParaResponse(usuario.getPessoa()));
        response.setPerfis(usuario.getPerfis().stream()
                .map(perfil -> new PerfilResponse(
                        perfil.getId(),
                        perfil.getNome(),
                        perfil.getDescricao()))
                .collect(Collectors.toSet()));
        return response;
    }

    public static Usuario formParaEntidade(UsuarioForm form, Set<Perfil> perfis) {
        return Usuario.builder()
                .email(form.getEmail())
                .senha(form.getSenha())
                .pessoa(PessoaMapper.entidadeParaForm(form))
                .perfis(perfis)
                .build();
    }
}
