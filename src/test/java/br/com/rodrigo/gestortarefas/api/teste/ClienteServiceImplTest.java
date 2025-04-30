package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.ClienteForm;
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
        pessoa.setUsuario(usuario);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setPessoa(pessoa);
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

    }

    @Test
    void deletar_DeveExcluirCliente() {

    }

    @Test
    void consultarPorId_DeveRetornarCliente() {

    }

    @Test
    void buscar_DeveRetornarPaginaDeClientes() {

    }

    @Test
    void atualizar_QuandoClienteNaoEncontrado_DeveLancarExcecao() {

    }
}