package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.TarefaRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.impl.UsuarioServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TarefaRepository tarefaRepository;

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

        Perfil perfil = new Perfil();
        perfil.setId(1L);
        perfil.setNome("ROLE_USER");
        perfil.setDescricao("Usuário comum");

        perfilResponse = new PerfilResponse();
        perfilResponse.setId(1L);
        perfilResponse.setNome("ROLE_USER");
        perfilResponse.setDescricao("Usuário comum");

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
        when(perfilService.consultarPorId(any())).thenReturn(Optional.of(perfilResponse));
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(usuarioRepository.existsByPessoaCpf(any())).thenReturn(false);

        UsuarioResponse resultado = usuarioService.criar(usuarioForm);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        assertEquals(usuario.getEmail(), resultado.getEmail());
        assertEquals(usuario.getPessoa().getNome(), resultado.getPessoa().getNome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void criar_QuandoEmailJaExiste_DeveLancarExcecao() {
        when(usuarioRepository.existsByEmailIgnoreCase(any())).thenReturn(true);

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> usuarioService.criar(usuarioForm));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveRetornarUsuarioAtualizado() {
        Long id = 1L;
        when(perfilService.consultarPorId(any())).thenReturn(Optional.of(perfilResponse));
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot(any(), any())).thenReturn(false);
        when(usuarioRepository.existsByPessoaCpfAndIdNot(any(), any())).thenReturn(false);

        UsuarioResponse resultado = usuarioService.atualizar(id, usuarioForm);

        assertNotNull(resultado);
        assertEquals(usuario.getId(), resultado.getId());
        assertEquals(usuario.getEmail(), resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deletar_QuandoUsuarioNaoTemTarefas_DeveDeletarUsuario() {
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(tarefaRepository.existsByResponsavelId(id)).thenReturn(false);

        assertDoesNotThrow(() -> usuarioService.deletar(id));

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void deletar_QuandoUsuarioTemTarefas_DeveLancarExcecao() {
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(tarefaRepository.existsByResponsavelId(id)).thenReturn(true);

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> usuarioService.deletar(id));
        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void consultarPorId_QuandoUsuarioExiste_DeveRetornarUsuario() {
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponse> resultado = usuarioService.consultarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getId(), resultado.get().getId());
        assertEquals(usuario.getEmail(), resultado.get().getEmail());
    }

    @Test
    void listarTodos_DeveRetornarPaginaComUsuarios() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(
                Collections.singletonList(usuario),
                pageable,
                1
        );
        when(usuarioRepository.findAll(pageable)).thenReturn(usuariosPage);

        Page<UsuarioResponse> resultado = usuarioService.listarTodos(pageable);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(usuario.getId(), resultado.getContent().get(0).getId());
        assertEquals(usuario.getEmail(), resultado.getContent().get(0).getEmail());
    }

    @Test
    void buscar_DeveRetornarPaginaFiltrada() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuariosPage = new PageImpl<>(
                Collections.singletonList(usuario),
                pageable,
                1
        );
        when(usuarioRepository.findAll(any(), any(), any(), any(), any())).thenReturn(usuariosPage);

        Page<UsuarioResponse> resultado = usuarioService.buscar(0, 10, "id",
                "teste@email.com", "Usuario Teste", "12345678900", 1L);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(usuario.getId(), resultado.getContent().get(0).getId());
        assertEquals(usuario.getEmail(), resultado.getContent().get(0).getEmail());
    }
}
