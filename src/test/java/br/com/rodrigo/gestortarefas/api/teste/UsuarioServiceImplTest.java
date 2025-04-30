package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.impl.UsuarioServiceImpl;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ValidadorUtil validadorUtil;

    @Mock
    private IPerfil perfilService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioForm usuarioForm;
    private Usuario usuario;
    private PerfilResponse perfilResponse;

    @BeforeEach
    void setUp() {
        usuarioForm = new UsuarioForm();
        usuarioForm.setEmail("teste@email.com");
        usuarioForm.setSenha("123456");
        usuarioForm.setNome("Usuario Teste");
        usuarioForm.setCpf("12345678900");
        usuarioForm.setDataNascimento(LocalDate.now());
        usuarioForm.setTelefone("11999999999");
        usuarioForm.setPerfil(1L);
        usuarioForm.setEmpresa(1L);

        Perfil perfil = new Perfil();
        perfil.setId(1L);
        perfil.setNome("ROLE_USER");
        perfil.setDescricao("Usuário comum");

        perfilResponse = new PerfilResponse();
        perfilResponse.setId(1L);
        perfilResponse.setNome("ROLE_USER");
        perfilResponse.setDescricao("Usuário comum");

        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setSenha("encoded_password");
        usuario.setPessoa(new Pessoa());
        usuario.getPessoa().setNome("Usuario Teste");
        usuario.getPessoa().setCpf("12345678900");
        usuario.getPessoa().setDataNascimento(LocalDate.now());
        usuario.getPessoa().setTelefone("11999999999");
        usuario.setPerfis(Set.of(perfil));
    }

    @Test
    void criar_DeveRetornarUsuarioCriado() {

    }

    @Test
    void criar_QuandoEmailJaExiste_DeveLancarExcecao() {

    }

    @Test
    void atualizar_DeveRetornarUsuarioAtualizado() {

    }

    @Test
    void consultarPorId_QuandoUsuarioExiste_DeveRetornarUsuario() {

    }

    @Test
    void listarTodos_DeveRetornarPaginaComUsuarios() {

    }

    @Test
    void buscar_DeveRetornarPaginaFiltrada() {

    }
}
