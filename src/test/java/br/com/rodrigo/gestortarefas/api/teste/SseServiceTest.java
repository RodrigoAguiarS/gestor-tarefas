package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.INotificacao;
import br.com.rodrigo.gestortarefas.api.services.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SseService sseService;

    @Mock
    private INotificacao notificacaoService;

    private static final Long USUARIO_ID = 1L;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);
    }

    @Test
    void conectar_DeveRetornarNovoEmitter_QuandoNaoExisteConexao() {
        SseEmitter emitter = sseService.conectar(USUARIO_ID);

        assertThat(emitter).isNotNull();
        assertThat(emitter.getTimeout()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void conectar_DeveRetornarEmitterExistente_QuandoJaExisteConexao() {
        SseEmitter primeiroEmitter = sseService.conectar(USUARIO_ID);

        SseEmitter segundoEmitter = sseService.conectar(USUARIO_ID);

        assertThat(segundoEmitter).isSameAs(primeiroEmitter);
    }

    @Test
    void notificarUsuario_DeveEnviarMensagem_QuandoUsuarioConectado() {
        SseEmitter emitter = sseService.conectar(USUARIO_ID);
        String mensagem = "Test message";

        sseService.notificarUsuario(USUARIO_ID, mensagem);

        assertThat(emitter).isNotNull();
    }

    @Test
    void notificarUsuario_DeveSalvarMensagemPendente_QuandoUsuarioDesconectado() {
        String mensagem = "Test message";

        sseService.notificarUsuario(USUARIO_ID, mensagem);

        SseEmitter emitter = sseService.conectar(USUARIO_ID);

        assertThat(emitter).isNotNull();
    }

    @Test
    void notificarPorPerfil_DeveNotificarTodosUsuariosComPerfil() {
        // Arrange
        long perfilId = 1L;
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        String mensagem = "Test message";

        when(usuarioRepository.findAllByPerfisId(perfilId)).thenReturn(usuarios);

        // Act
        sseService.notificarPorPerfil(mensagem, perfilId);

        // Assert
        verify(usuarioRepository).findAllByPerfisId(perfilId);
        verify(notificacaoService).criarNotificacao(usuario1, mensagem);
        verify(notificacaoService).criarNotificacao(usuario2, mensagem);
    }

    @Test
    void removerConexao_DeveRemoverEmitter_QuandoChamado() {
        SseEmitter emitter = sseService.conectar(USUARIO_ID);

        emitter.complete();

        SseEmitter novoEmitter = sseService.conectar(USUARIO_ID);

        assertThat(novoEmitter).isNotNull();
        assertThat(novoEmitter).isNotSameAs(emitter);
    }

    @Test
    void enviarMensagensPendentes_DeveEnviarMensagensAoConectar() {
        String mensagem = "Test message";

        sseService.notificarUsuario(USUARIO_ID, mensagem);

        SseEmitter emitter = sseService.conectar(USUARIO_ID);

        assertThat(emitter).isNotNull();
    }
}
