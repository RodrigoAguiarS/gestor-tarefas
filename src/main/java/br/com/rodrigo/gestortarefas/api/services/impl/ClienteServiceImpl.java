package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.ClienteMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.EmpresaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.services.ICliente;
import br.com.rodrigo.gestortarefas.api.services.IEmpresa;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ICliente {

    private final ClienteRepository clienteRepository;
    private final IEmpresa empresaService;
    private final PasswordEncoder passwordEncoder;
    private final IPerfil perfilService;
    private final IUsuario usuarioService;
    private final ValidadorUtil validadorUtil;


    @Override
    public ClienteResponse criar(Long idCliente, ClienteForm clienteForm) {
        Cliente cliente = criarCliente(clienteForm, idCliente);
        validadorUtil.validarCpfUnico(clienteForm.getCpf(), cliente.getId());
        validadorUtil.validarEmailUnico(clienteForm.getEmail(), cliente.getPessoa().getUsuario().getId());
        cliente = clienteRepository.save(cliente);
        return construirDto(cliente);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(id)));

        cliente.desativar();
        cliente.getPessoa().desativar();
        cliente.getPessoa().getUsuario().desativar();

        clienteRepository.save(cliente);
    }

    @Override
    public Optional<ClienteResponse> consultarPorId(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .map(this::construirDto);
    }

    public Page<ClienteResponse> buscar(int page, int size, String sort, String email,
                                        String nome, String cpf, String cidade, String estado, String cep) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Cliente> clientes = clienteRepository.findAll(email, nome, cpf, cidade, estado, cep, pageable);
        return clientes.map(this::construirDto);
    }

    @Override
    public Optional<ClienteResponse> getClienteLogado() {
        UsuarioResponse usuario = usuarioService.obterUsuarioLogado();
        Cliente cliente = clienteRepository.findById(usuario.getId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(usuario.getId())));
        return Optional.of(construirDto(cliente));
    }

    private Cliente criarCliente(ClienteForm clienteForm, Long idCliente) {
        Cliente cliente = idCliente != null ? clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(idCliente))) : new Cliente();

        PerfilResponse perfilResponse = perfilService.consultarPorId(Perfil.CLIENTE)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(Perfil.CLIENTE)));

        EmpresaResponse empresa = empresaService.consultarPorId(1L)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.EMPRESA_NAO_ENCONTRADA.getMessage(1L)));

        Empresa empresaCliente = Empresa.builder()
                .id(empresa.getId())
                .nome(empresa.getNome())
                .cnpj(empresa.getCnpj())
                .telefone(empresa.getTelefone())
                .endereco(empresa.getEndereco())
                .build();

        Perfil perfilCliente = Perfil.builder()
                .id(perfilResponse.getId())
                .nome(perfilResponse.getNome())
                .descricao(perfilResponse.getDescricao())
                .build();

        Usuario usuario = cliente.getPessoa() != null && cliente.getPessoa().getUsuario() != null ?
                cliente.getPessoa().getUsuario() : new Usuario();
        usuario.setEmail(clienteForm.getEmail());
        if (clienteForm.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(clienteForm.getSenha()));
        }
        usuario.setPerfis(Set.of(perfilCliente));
        usuario.setEmpresa(empresaCliente);

        Pessoa pessoa = cliente.getPessoa() != null ? cliente.getPessoa() : new Pessoa();
        pessoa.setNome(clienteForm.getNome());
        pessoa.setCpf(clienteForm.getCpf());
        pessoa.setDataNascimento(clienteForm.getDataNascimento());
        pessoa.setTelefone(clienteForm.getTelefone());
        pessoa.setUsuario(usuario);

        Endereco endereco = cliente.getEndereco() != null ? cliente.getEndereco() : new Endereco();
        endereco.setRua(clienteForm.getRua());
        endereco.setNumero(clienteForm.getNumero());
        endereco.setBairro(clienteForm.getBairro());
        endereco.setCidade(clienteForm.getCidade());
        endereco.setEstado(clienteForm.getEstado());
        endereco.setCep(clienteForm.getCep());

        cliente.setPessoa(pessoa);
        cliente.setEndereco(endereco);

        return cliente;
    }

    private ClienteResponse construirDto(Cliente cliente) {
        return ClienteMapper.entidadeParaResponse(cliente);
    }
}
