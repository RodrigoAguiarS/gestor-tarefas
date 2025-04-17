package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.TarefaRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
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
        verificarUnicidadeEmailCpf(usuarioForm.getEmail(), usuarioForm.getCpf(), id);

        Usuario usuario = id == null ? new Usuario() : usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id)));

        Optional<PerfilResponse> perfilResponse = perfilService.consultarPorId(usuarioForm.getPerfil());

        if(perfilResponse.isPresent()) {
            Perfil perfil = new Perfil();
            perfil.setId(perfilResponse.get().getId());
            perfil.setNome(perfilResponse.get().getNome());
            perfil.setDescricao(perfilResponse.get().getDescricao());
            usuario.setPerfis(Collections.singleton(perfil));
        } else {
            throw new ObjetoNaoEncontradoException(MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(usuarioForm.getPerfil()));
        }

        usuario.setSenha(passwordEncoder.encode(usuarioForm.getSenha()));
        usuario.setEmail(usuarioForm.getEmail());
        usuario.getPessoa().setNome(usuarioForm.getNome());
        usuario.getPessoa().setCpf(usuarioForm.getCpf());
        usuario.getPessoa().setDataNascimento(usuarioForm.getDataNascimento());
        usuario.getPessoa().setTelefone(usuarioForm.getTelefone());

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

    private void verifcarTarefaVinculadoUsuario(Long idUsuario) {
        if (tarefaRepository.existsByResponsavelId(idUsuario)) {
            throw new ViolacaoIntegridadeDadosException(MensagensError.USUARIO_POSSUI_TAREFA.getMessage());
        }
    }

    private UsuarioResponse construirDto(Usuario usuario) {
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(usuario.getId());
        usuarioResponse.setEmail(usuario.getEmail());
        usuarioResponse.getPessoa().setNome(usuario.getPessoa().getNome());
        usuarioResponse.getPessoa().setCpf(usuario.getPessoa().getCpf());
        usuarioResponse.getPessoa().setDataNascimento(usuario.getPessoa().getDataNascimento());
        usuarioResponse.getPessoa().setTelefone(usuario.getPessoa().getTelefone());
        usuarioResponse.setPerfis(usuario.getPerfis().stream()
                .map(perfil -> new PerfilResponse(perfil.getId(), perfil.getNome(), perfil.getDescricao()))
                .collect(Collectors.toSet()));
        return usuarioResponse;
    }
}