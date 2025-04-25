package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.UsuarioMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.TarefaRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuario {

    private final PasswordEncoder passwordEncoder;
    private final IPerfil perfilService;
    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final ValidadorUtil validadorUtil;

    @Override
    public UsuarioResponse criar(UsuarioForm usuarioForm) {
        Usuario usuario = criarEntidade(usuarioForm, null);
        usuario = usuarioRepository.save(usuario);
        return construirDto(usuario);
    }

    @Override
    public UsuarioResponse atualizar(Long id, UsuarioForm form) {
        Usuario usuario = criarEntidade(form, id);
        usuario = usuarioRepository.save(usuario);
        return construirDto(usuario);
    }

    @Override
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id)));
        verifcarTarefaVinculadoUsuario(usuario.getId());
        usuarioRepository.delete(usuario);
    }

    @Override
    public Optional<UsuarioResponse> consultarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<UsuarioResponse> buscar(int page, int size, String sort, String email,
                                        String nome, String cpf, Long perfilId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Usuario> usuarios = usuarioRepository.findAll(email, nome, cpf, perfilId, pageable);
        return usuarios.map(this::construirDto);
    }

    @Override
    @Cacheable("usuarios")
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        return usuarios.map(this::construirDto);
    }

    private Usuario criarEntidade(UsuarioForm usuarioForm, Long id) {
        validadorUtil.validarEmailUnico(usuarioForm.getEmail(), id);
        validadorUtil.validarCpfUnico(usuarioForm.getCpf(), id);

        Usuario usuario = id != null ? usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id))) : new Usuario();

        Perfil perfil = perfilService.consultarPorId(usuarioForm.getPerfil())
                .map(perfilResponse -> {
                    Perfil p = new Perfil();
                    p.setId(perfilResponse.getId());
                    p.setNome(perfilResponse.getNome());
                    p.setDescricao(perfilResponse.getDescricao());
                    return p;
                })
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(usuarioForm.getPerfil())));

        usuario.getPessoa().setNome(usuarioForm.getNome());
        usuario.getPessoa().setDataNascimento(usuarioForm.getDataNascimento());
        usuario.getPessoa().setTelefone(usuarioForm.getTelefone());
        usuario.getPessoa().setCpf(usuarioForm.getCpf());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioForm.getSenha()));
        usuario.setPerfis(Collections.singleton(perfil));

        return usuario;
    }

    @Override
    public Usuario obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return obterUsuarioPorEmail(authentication.getName());
    }

    @Override
    public List<String> obterPerfis(String email) {
        Usuario usuario = obterUsuarioPorEmail(email);
        return usuario.getPerfis().stream().map(Perfil::getNome).collect(Collectors.toList());
    }

    public Usuario obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_LOGIN.getMessage(email)));
    }

    private void verifcarTarefaVinculadoUsuario(Long idUsuario) {
        if (tarefaRepository.existsByResponsavelId(idUsuario)) {
            throw new ViolacaoIntegridadeDadosException(MensagensError.USUARIO_POSSUI_TAREFA.getMessage());
        }
    }

    private UsuarioResponse construirDto(Usuario usuario) {
        return UsuarioMapper.entidadeParaResponse(usuario);
    }
}