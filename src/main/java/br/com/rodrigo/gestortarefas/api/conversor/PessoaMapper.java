package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PessoaResponse;

public class PessoaMapper {

    public static PessoaResponse entidadeParaResponse(Pessoa pessoa) {
        return PessoaResponse.builder()
                .id(pessoa.getId())
                .nome(pessoa.getNome())
                .dataNascimento(pessoa.getDataNascimento())
                .cpf(pessoa.getCpf())
                .telefone(pessoa.getTelefone())
                .build();
    }

    public static Pessoa formParaEntidade(UsuarioForm form) {
        return Pessoa.builder()
                .nome(form.getNome())
                .telefone(form.getTelefone())
                .cpf(form.getCpf())
                .dataNascimento(form.getDataNascimento())
                .build();
    }

    public static Pessoa responseParaEntidade(PessoaResponse response) {
        return Pessoa.builder()
                .id(response.getId())
                .nome(response.getNome())
                .telefone(response.getTelefone())
                .cpf(response.getCpf())
                .dataNascimento(response.getDataNascimento())
                .build();
    }
}
