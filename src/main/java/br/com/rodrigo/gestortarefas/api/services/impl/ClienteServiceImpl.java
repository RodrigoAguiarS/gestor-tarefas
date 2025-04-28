package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.ClienteMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.services.ICliente;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ICliente {

    private final ClienteRepository clienteRepository;
    private final ValidadorUtil validadorUtil;
    private final IUsuario usuarioService;

    @Override
    public ClienteResponse criar(Long idCliente, ClienteForm clienteForm) {
        Cliente cliente = criaCliente(clienteForm, idCliente);
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
    public Optional<ClienteResponse> getClienteLogado() {
        Usuario usuario = usuarioService.obterUsuarioLogado();

        Cliente cliente = clienteRepository.findClienteByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(usuario.getId())));

        return Optional.of(ClienteMapper.entidadeParaResponse(cliente));
    }

    @Override
    public Page<ClienteResponse> buscar(int page, int size, String sort, String email,
                                        String nome, String cpf, String cidade, String estado, String cep) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Cliente> clientes = clienteRepository.findAll(email, nome, cpf, cidade, estado, cep, pageable);
        return clientes.map(this::construirDto);
    }

    private Cliente criaCliente(ClienteForm clienteForm, Long idCliente) {
        Cliente cliente = idCliente == null ? new Cliente() : clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(idCliente)));

        Long idUsuario = cliente.getUsuario() != null ? cliente.getUsuario().getId() : null;

        validadorUtil.validarEmailUnico(clienteForm.getEmail(), idUsuario);
        validadorUtil.validarCpfUnico(clienteForm.getCpf(), idUsuario);

        UsuarioForm usuarioForm = UsuarioForm.builder()
                .email(clienteForm.getEmail())
                .senha(clienteForm.getSenha())
                .dataNascimento(clienteForm.getDataNascimento())
                .perfil(Perfil.CLIENTE)
                .nome(clienteForm.getNome())
                .cpf(clienteForm.getCpf())
                .telefone(clienteForm.getTelefone())
                .build();

        UsuarioResponse usuarioResponse = usuarioService.criar(idUsuario, usuarioForm);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioResponse.getId());
        cliente.setUsuario(usuario);

        cliente.setEndereco(ClienteMapper.formParaEntidade(clienteForm, usuario).getEndereco());

        return cliente;
    }

    private ClienteResponse construirDto(Cliente cliente) {
        return ClienteMapper.entidadeParaResponse(cliente);
    }
}
