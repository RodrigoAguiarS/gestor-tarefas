package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;

public class ClienteMapper {

    public static ClienteResponse entidadeParaResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getPessoa().getUsuario().getEmail(),
                cliente.getPessoa().getNome(),
                cliente.getPessoa().getTelefone(),
                cliente.getPessoa().getCpf(),
                cliente.getPessoa().getDataNascimento(),
                cliente.getEndereco().getRua(),
                cliente.getEndereco().getNumero(),
                cliente.getEndereco().getBairro(),
                cliente.getEndereco().getCidade(),
                cliente.getEndereco().getEstado(),
                cliente.getEndereco().getCep()
        );
    }

    public static Cliente formParaEntidade(ClienteForm form, Pessoa pessoa) {
        return Cliente.builder()
                .pessoa(pessoa)
                .endereco(Endereco.builder()
                        .rua(form.getRua())
                        .numero(form.getNumero())
                        .bairro(form.getBairro())
                        .cidade(form.getCidade())
                        .estado(form.getEstado())
                        .cep(form.getCep())
                        .build())
                .build();
    }

    public static Cliente responseParaEntidade(ClienteResponse response) {
        Cliente cliente = new Cliente();
        cliente.setId(response.getId());
        cliente.setPessoa(new Pessoa());
        cliente.getPessoa().setUsuario(new Usuario());
        cliente.getPessoa().getUsuario().setEmail(response.getEmail());
        cliente.getPessoa().setNome(response.getNome());
        cliente.getPessoa().setTelefone(response.getTelefone());
        cliente.getPessoa().setCpf(response.getCpf());
        cliente.getPessoa().setDataNascimento(response.getDataNascimento());
        cliente.setEndereco(new Endereco());
        cliente.getEndereco().setRua(response.getRua());
        cliente.getEndereco().setNumero(response.getNumero());
        cliente.getEndereco().setBairro(response.getBairro());
        cliente.getEndereco().setCidade(response.getCidade());
        cliente.getEndereco().setEstado(response.getEstado());
        cliente.getEndereco().setCep(response.getCep());
        return cliente;

    }
}
