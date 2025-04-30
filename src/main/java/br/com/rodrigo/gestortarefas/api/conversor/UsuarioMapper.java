package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.PessoaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;

import java.util.stream.Collectors;

public class UsuarioMapper {

    public static UsuarioResponse entidadeParaResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .pessoa(pessoaParaResponse(usuario.getPessoa()))
                .empresa(EmpresaMapper.entidadeParaResponse(usuario.getEmpresa()))
                .perfis(usuario.getPerfis().stream()
                        .map(PerfilMapper::entidadeParaResponse)
                        .collect(Collectors.toSet()))
                .ativo(usuario.getAtivo())
                .build();
    }

    private static PessoaResponse pessoaParaResponse(Pessoa pessoa) {
        if (pessoa == null) {
            return null;
        }

        return PessoaResponse.builder()
                .id(pessoa.getId())
                .nome(pessoa.getNome())
                .cpf(pessoa.getCpf())
                .telefone(pessoa.getTelefone())
                .dataNascimento(pessoa.getDataNascimento())
                .build();
    }
}
