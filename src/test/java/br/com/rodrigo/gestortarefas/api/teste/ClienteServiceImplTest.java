package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.ICategoria;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import br.com.rodrigo.gestortarefas.api.services.impl.ClienteServiceImpl;
import br.com.rodrigo.gestortarefas.api.services.impl.ProdutoServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private IPerfil perfilService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidadorUtil validadorUtil;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteForm clienteForm;
    private PerfilResponse perfilResponse;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setEndereco(new Endereco());
        cliente.setUsuario(new Usuario());
        cliente.getUsuario().setPessoa(new Pessoa());
        cliente.getUsuario().setPerfis(new HashSet<>());
        cliente.getUsuario().setEmail("teste@email.com");
        cliente.getUsuario().getPessoa().setNome("Test");
        cliente.getUsuario().getPessoa().setCpf("12345678900");

        clienteForm = new ClienteForm();
        clienteForm.setEmail("teste@email.com");
        clienteForm.setNome("Test");
        clienteForm.setCpf("12345678900");
        clienteForm.setSenha("senha123");

        perfilResponse = new PerfilResponse();
        perfilResponse.setId(Perfil.CLIENTE);
        perfilResponse.setNome("CLIENTE");
    }

    @Test
    void criar_DeveRetornarClienteCriado() {
        when(perfilService.consultarPorId(any())).thenReturn(Optional.of(perfilResponse));
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        doNothing().when(validadorUtil).validarEmailUnico(anyString(), any());
        doNothing().when(validadorUtil).validarCpfUnico(anyString(), any());

        ClienteResponse resultado = clienteService.criar(clienteForm);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getUsuario().getEmail(), resultado.getEmail());
        assertEquals(cliente.getUsuario().getPessoa().getNome(), resultado.getNome());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(validadorUtil, times(1)).validarEmailUnico(clienteForm.getEmail(), null);
        verify(validadorUtil, times(1)).validarCpfUnico(clienteForm.getCpf(), null);
    }

    @Test
    void criar_QuandoEmailJaExiste_DeveLancarExcecao() {
        doThrow(new ViolacaoIntegridadeDadosException("Email já cadastrado"))
                .when(validadorUtil).validarEmailUnico(anyString(), any());

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> clienteService.criar(clienteForm));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveRetornarClienteAtualizado() {
        Long id = 1L;
        when(perfilService.consultarPorId(any())).thenReturn(Optional.of(perfilResponse));
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        doNothing().when(validadorUtil).validarEmailUnico(anyString(), any());
        doNothing().when(validadorUtil).validarCpfUnico(anyString(), any());

        ClienteResponse resultado = clienteService.atualizar(id, clienteForm);

        assertNotNull(resultado);
        assertEquals(cliente.getId(), resultado.getId());
        assertEquals(cliente.getUsuario().getEmail(), resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(validadorUtil, times(1)).validarEmailUnico(clienteForm.getEmail(), id);
        verify(validadorUtil, times(1)).validarCpfUnico(clienteForm.getCpf(), id);
    }

    @Test
    void atualizar_QuandoClienteNaoEncontrado_DeveLancarExcecao() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class,
                () -> clienteService.atualizar(id, clienteForm));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void deletar_DeveExcluirCliente() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).deleteById(id);

        clienteService.deletar(id);

        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    void deletar_QuandoClienteNaoEncontrado_DeveLancarExcecao() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class,
                () -> clienteService.deletar(id));
        verify(clienteRepository, never()).deleteById(any());
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
        Page<Cliente> clientePage = new PageImpl<>(List.of(cliente));

        when(clienteRepository.findAll(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(clientePage);

        Page<ClienteResponse> resultado = clienteService.buscar(0, 10, "id",
                "email", "nome", "cpf", "cidade", "estado", "cep");

        assertNotNull(resultado);
        assertFalse(resultado.getContent().isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(cliente.getId(), resultado.getContent().get(0).getId());
    }
}