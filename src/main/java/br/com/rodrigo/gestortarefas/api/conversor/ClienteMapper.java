package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;

public class ClienteMapper {

    public static ClienteResponse entidadeParaResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                UsuarioMapper.entidadeParaResponse(cliente.getPessoa().getUsuario()),
                cliente.getEndereco()
        );
    }

    public static Cliente responseParaEntidade(ClienteResponse response) {
        Cliente cliente = new Cliente();
        cliente.setId(response.getId());
        cliente.setPessoa(mapearPessoa(response));
        cliente.setEndereco(mapearEndereco(response));
        return cliente;
    }

    private static Pessoa mapearPessoa(ClienteResponse response) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(response.getId());
        pessoa.setNome(response.getUsuario().getPessoa().getNome());
        pessoa.setTelefone(response.getUsuario().getPessoa().getTelefone());
        pessoa.setCpf(response.getUsuario().getPessoa().getCpf());
        pessoa.setDataNascimento(response.getUsuario().getPessoa().getDataNascimento());
        pessoa.setUsuario(mapearUsuario(response));
        return pessoa;
    }

    private static Usuario mapearUsuario(ClienteResponse response) {
        Usuario usuario = new Usuario();
        usuario.setId(response.getUsuario().getId());
        usuario.setEmail(response.getUsuario().getEmail());
        usuario.setEmpresa(mapearEmpresa(response));
        return usuario;
    }

    private static Empresa mapearEmpresa(ClienteResponse response) {
        Empresa empresa = new Empresa();
        empresa.setId(response.getUsuario().getEmpresa().getId());
        empresa.setNome(response.getUsuario().getEmpresa().getNome());
        return empresa;
    }

    private static Endereco mapearEndereco(ClienteResponse response) {
        Endereco endereco = new Endereco();
        endereco.setRua(response.getEndereco().getRua());
        endereco.setNumero(response.getEndereco().getNumero());
        endereco.setBairro(response.getEndereco().getBairro());
        endereco.setCidade(response.getEndereco().getCidade());
        endereco.setEstado(response.getEndereco().getEstado());
        endereco.setCep(response.getEndereco().getCep());
        return endereco;
    }
}