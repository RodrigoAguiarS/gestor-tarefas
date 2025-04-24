package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.services.impl.ClienteServiceImpl;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private IUsuario usuarioService;

    @Mock
    private ValidadorUtil validadorUtil;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteForm clienteForm;
    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        // Configuração do cliente
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Test");
        pessoa.setCpf("12345678900");
        usuario.setPessoa(pessoa);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setUsuario(usuario);
        cliente.setEndereco(new Endereco());

        // Configuração do form
        clienteForm = new ClienteForm();
        clienteForm.setEmail("teste@email.com");
        clienteForm.setNome("Test");
        clienteForm.setCpf("12345678900");
        clienteForm.setSenha("senha123");
        clienteForm.setRua("Rua Teste");
        clienteForm.setNumero("123");
        clienteForm.setBairro("Bairro Teste");
        clienteForm.setCidade("Cidade Teste");
        clienteForm.setEstado("Estado Teste");
        clienteForm.setCep("12345-678");

        // Configuração do usuarioResponse
        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setEmail("teste@email.com");
    }

    @Test
    void criar_DeveRetornarClienteCriado() {
        when(usuarioService.criar(any(UsuarioForm.class))).thenReturn(usuarioResponse);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        doNothing().when(validadorUtil).validarEmailUnico(anyString(), any());
        doNothing().when(validadorUtil).validarCpfUnico(anyString(), any());

        ClienteResponse resultado = clienteService.criar(clienteForm);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(clienteForm.getEmail(), resultado.getEmail());
        assertEquals(clienteForm.getNome(), resultado.getNome());
        verify(usuarioService, times(1)).criar(any(UsuarioForm.class));
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void atualizar_DeveRetornarClienteAtualizado() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(usuarioService.atualizar(any(), any(UsuarioForm.class))).thenReturn(usuarioResponse);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        doNothing().when(validadorUtil).validarEmailUnico(anyString(), any());
        doNothing().when(validadorUtil).validarCpfUnico(anyString(), any());

        ClienteResponse resultado = clienteService.atualizar(id, clienteForm);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(clienteForm.getEmail(), resultado.getEmail());
        verify(usuarioService, times(1)).atualizar(any(), any(UsuarioForm.class));
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deletar_DeveExcluirCliente() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).deleteById(id);

        assertDoesNotThrow(() -> clienteService.deletar(id));
        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    void consultarPorId_DeveRetornarCliente() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<ClienteResponse> resultado = clienteService.consultarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(cliente.getId(), resultado.get().getId());
        assertEquals(cliente.getUsuario().getEmail(), resultado.get().getEmail());
    }

    @Test
    void buscar_DeveRetornarPaginaDeClientes() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Cliente> clientePage = new PageImpl<>(Collections.singletonList(cliente));

        when(clienteRepository.findAll(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(clientePage);

        Page<ClienteResponse> resultado = clienteService.buscar(0, 10, "id",
                "email", "nome", "cpf", "cidade", "estado", "cep");

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(cliente.getId(), resultado.getContent().get(0).getId());
    }

    @Test
    void criar_QuandoEmailJaExiste_DeveLancarExcecao() {
        doThrow(new ViolacaoIntegridadeDadosException("Email já cadastrado"))
                .when(validadorUtil).validarEmailUnico(anyString(), any());

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> clienteService.criar(clienteForm));
        verify(clienteRepository, never()).save(any());
        verify(usuarioService, never()).criar(any());
    }

    @Test
    void atualizar_QuandoClienteNaoEncontrado_DeveLancarExcecao() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class,
                () -> clienteService.atualizar(id, clienteForm));
        verify(clienteRepository, never()).save(any());
        verify(usuarioService, never()).atualizar(any(), any());
    }
}