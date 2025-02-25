package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
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
public class UsuarioServiceImpl extends GenericServiceImpl<Usuario, UsuarioForm, UsuarioResponse> {

    private final PasswordEncoder passwordEncoder;
    private final PerfilServiceImpl perfilService;
    private final SmsService smsService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(PasswordEncoder passwordEncoder,
                              PerfilServiceImpl perfilService,
                              SmsService smsService, UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.passwordEncoder = passwordEncoder;
        this.perfilService = perfilService;
        this.smsService = smsService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected String getEntidadeNome() {
        return "Usuário";
    }

    @Override
    protected Usuario criarEntidade(UsuarioForm usuarioForm, Long id) {
        verificarUnicidadeEmailCpf(usuarioForm.getEmail(), usuarioForm.getCpf(), id);
        smsService.enviarSenhaSms("+55" + usuarioForm.getTelefone(), usuarioForm.getSenha());
        Usuario usuario = buscarOuCriarUsuario(id);
        mapearDadosUsuario(usuarioForm, usuario);
        configurarPerfis(usuario, usuarioForm.getPerfil());
        return usuario;
    }

    @Override
    public UsuarioResponse atualizar(Long id, UsuarioForm form) {
        Usuario entidadeExistente = repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));
        verificarUnicidadeEmailCpf(form.getEmail(), form.getCpf(), id);
        mapearDadosUsuario(form, entidadeExistente);
        configurarPerfis(entidadeExistente, form.getPerfil());
        entidadeExistente = repository.save(entidadeExistente);
        smsService.enviarSenhaSms("+55" + form.getTelefone(), form.getSenha());
        return construirDto(entidadeExistente);
    }

    public Usuario obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return obterUsuarioPorEmail(authentication.getName());
    }

    public Page<UsuarioResponse> listarTodos(int page, int size, String sort, String email,
                                             String nome, String cpf, Long perfilId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Usuario> usuarios = usuarioRepository.findAll(email, nome, cpf, perfilId, pageable);
        return usuarios.map(usuario -> ModelMapperUtil.map(usuario, UsuarioResponse.class));
    }

    private Usuario buscarOuCriarUsuario(Long id) {
        return (id != null) ? repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id)))
                : new Usuario();
    }

    private void mapearDadosUsuario(UsuarioForm usuarioForm, Usuario usuario) {
        ModelMapperUtil.map(usuarioForm, usuario);
        usuario.setSenha(passwordEncoder.encode(usuarioForm.getSenha()));

        if (usuario.getPessoa() == null) {
            usuario.setPessoa(new Pessoa());
        }

        ModelMapperUtil.map(usuarioForm, usuario.getPessoa());
    }

    private void configurarPerfis(Usuario usuario, Long perfilId) {
        Optional<PerfilResponse> perfilResponse = perfilService.consultarPorId(perfilId);
        if (perfilResponse.isPresent()) {
            Perfil perfil = ModelMapperUtil.map(perfilResponse.get(), Perfil.class);
            usuario.setPerfis(Collections.singleton(perfil));
        } else {
            throw new ObjetoNaoEncontradoException(MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(perfilId));
        }
    }

    @Override
    public UsuarioResponse construirDto(Usuario usuario) {
        return ModelMapperUtil.map(usuario, UsuarioResponse.class);
    }

    @Override
    protected void ativar(Usuario usuario) {
        usuario.ativar();
    }

    @Override
    protected void desativar(Usuario usuario) {
        usuario.desativar();
    }

    public List<String> obterPerfis(String email) {
        Usuario usuario = obterUsuarioPorEmail(email);
        return usuario.getPerfis().stream().map(Perfil::getNome).collect(Collectors.toList());
    }

    public Usuario obterUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_LOGIN.getMessage(email)));
    }

    private void verificarUnicidadeEmailCpf(String email, String cpf, Long id) {
        if (id == null) {
            if (usuarioRepository.existsByEmailIgnoreCase(email)) {
                throw new ViolacaoIntegridadeDadosException(MensagensError.EMAIL_JA_CADASTRADO.getMessage(email));
            }
            if (usuarioRepository.existsByPessoaCpf(cpf)) {
                throw new ViolacaoIntegridadeDadosException(MensagensError.CPF_JA_CADASTRADO.getMessage(cpf));
            }
        } else {
            if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new ViolacaoIntegridadeDadosException(MensagensError.EMAIL_JA_CADASTRADO.getMessage(email));
            }
            if (usuarioRepository.existsByPessoaCpfAndIdNot(cpf, id)) {
                throw new ViolacaoIntegridadeDadosException(MensagensError.CPF_JA_CADASTRADO.getMessage(cpf));
            }
        }
    }
}
