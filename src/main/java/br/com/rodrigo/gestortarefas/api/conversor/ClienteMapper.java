package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;

public class ClienteMapper {

    public static ClienteResponse entidadeParaResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getUsuario().getEmail(),
                cliente.getUsuario().getPessoa().getNome(),
                cliente.getUsuario().getPessoa().getTelefone(),
                cliente.getUsuario().getPessoa().getCpf(),
                cliente.getUsuario().getPessoa().getDataNascimento(),
                cliente.getEndereco().getRua(),
                cliente.getEndereco().getNumero(),
                cliente.getEndereco().getBairro(),
                cliente.getEndereco().getCidade(),
                cliente.getEndereco().getEstado(),
                cliente.getEndereco().getCep()
        );
    }

    public static Cliente formParaEntidade(ClienteForm form, Usuario usuario) {
        return Cliente.builder()
                .usuario(usuario)
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
}
