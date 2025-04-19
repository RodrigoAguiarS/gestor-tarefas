package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.services.ICliente;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ICliente {

    private final ClienteRepository clienteRepository;
    private final IPerfil  perfilService;
    private final PasswordEncoder passwordEncoder;
    private final ValidadorUtil validadorUtil;

    @Override
    public ClienteResponse criar(ClienteForm clienteForm) {
        Cliente cliente = criaCliente(clienteForm, null);
        cliente = clienteRepository.save(cliente);
        return construirDto(cliente);
    }

    @Override
    public ClienteResponse atualizar(Long id, ClienteForm clienteForm) {
        Cliente cliente = criaCliente(clienteForm, id);
        cliente = clienteRepository.save(cliente);
        return construirDto(cliente);
    }

    @Override
    public void deletar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(id)));

        clienteRepository.deleteById(cliente.getId());
    }

    @Override
    public Optional<ClienteResponse> consultarPorId(Long id) {
        return clienteRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<ClienteResponse> buscar(int page, int size, String sort, String email,
                                        String nome, String cpf, String cidade, String estado, String cep) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Cliente> clientes = clienteRepository.findAll(email, nome, cpf, cidade, estado, cep, pageable);
        return clientes.map(this::construirDto);
    }

    private Cliente criaCliente(ClienteForm clienteForm, Long id) {

        validadorUtil.validarEmailUnico(clienteForm.getEmail(), id);
        validadorUtil.validarCpfUnico(clienteForm.getCpf(), id);

        Cliente cliente = id == null ? new Cliente() : clienteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id)));

        PerfilResponse perfilReponse = perfilService.consultarPorId(Perfil.CLIENTE)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(Perfil.CLIENTE)));

        Perfil perfil = new Perfil();
        perfil.setId(perfilReponse.getId());
        perfil.setNome(perfilReponse.getNome());
        perfil.setDescricao(perfilReponse.getDescricao());
        cliente.getEndereco().setRua(clienteForm.getRua());
        cliente.getEndereco().setNumero(clienteForm.getNumero());
        cliente.getEndereco().setBairro(clienteForm.getBairro());
        cliente.getEndereco().setCidade(clienteForm.getCidade());
        cliente.getEndereco().setEstado(clienteForm.getEstado());
        cliente.getEndereco().setCep(clienteForm.getCep());
        cliente.getUsuario().getPessoa().setNome(clienteForm.getNome());
        cliente.getUsuario().getPessoa().setCpf(clienteForm.getCpf());
        cliente.getUsuario().getPessoa().setDataNascimento(clienteForm.getDataNascimento());
        cliente.getUsuario().getPessoa().setTelefone(clienteForm.getTelefone());
        cliente.getUsuario().setEmail(clienteForm.getEmail());
        cliente.getUsuario().getPerfis().add(perfil);
        cliente.getUsuario().setSenha(passwordEncoder.encode(clienteForm.getSenha()));

        return cliente;
    }

    private ClienteResponse construirDto(Cliente cliente) {
        ClienteResponse clienteResponse = new ClienteResponse();
        clienteResponse.setId(cliente.getId());
        clienteResponse.setNome(cliente.getUsuario().getPessoa().getNome());
        clienteResponse.setEmail(cliente.getUsuario().getEmail());
        clienteResponse.setCpf(cliente.getUsuario().getPessoa().getCpf());
        clienteResponse.setDataNascimento(cliente.getUsuario().getPessoa().getDataNascimento());
        clienteResponse.setTelefone(cliente.getUsuario().getPessoa().getTelefone());
        clienteResponse.setRua(cliente.getEndereco().getRua());
        clienteResponse.setNumero(cliente.getEndereco().getNumero());
        clienteResponse.setBairro(cliente.getEndereco().getBairro());
        clienteResponse.setCidade(cliente.getEndereco().getCidade());
        clienteResponse.setEstado(cliente.getEndereco().getEstado());
        clienteResponse.setCep(cliente.getEndereco().getCep());

        return clienteResponse;
    }
}
