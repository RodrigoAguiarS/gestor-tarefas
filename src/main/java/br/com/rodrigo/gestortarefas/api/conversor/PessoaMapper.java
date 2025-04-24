package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PessoaResponse;

public class PessoaMapper {

    public static PessoaResponse entidadeParaResponse(Pessoa pessoa) {
        return new PessoaResponse(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getCpf(),
                pessoa.getTelefone()
        );
    }

    public static Pessoa entidadeParaForm(UsuarioForm form) {
        return Pessoa.builder()
                .nome(form.getNome())
                .telefone(form.getTelefone())
                .cpf(form.getCpf())
                .dataNascimento(form.getDataNascimento())
                .build();
    }
}
