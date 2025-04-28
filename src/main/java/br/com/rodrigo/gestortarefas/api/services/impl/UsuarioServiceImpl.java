package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.UsuarioMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Cliente;
import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.UsuarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.ClienteRepository;
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
    private final ValidadorUtil validadorUtil;
    private final ClienteRepository clienteRepository;

    @Override
    public UsuarioResponse criar(Long idUsuario, UsuarioForm usuarioForm) {
        Usuario usuario = criarEntidade(usuarioForm, idUsuario);
        usuario = usuarioRepository.save(usuario);
        return construirDto(usuario);
    }

    @Override
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(id)));

        Optional<Cliente> clienteOptional = clienteRepository.findClienteByUsuarioId(usuario.getId());
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            clienteRepository.delete(cliente);
        }
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

    private Usuario criarEntidade(UsuarioForm usuarioForm, Long idUsuario) {
        validadorUtil.validarEmailUnico(usuarioForm.getEmail(), idUsuario);
        validadorUtil.validarCpfUnico(usuarioForm.getCpf(), idUsuario);


        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(idUsuario))) : new Usuario();

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

        Empresa empresa = new Empresa();
        empresa.setId(2L);

        usuario.getPessoa().setNome(usuarioForm.getNome());
        usuario.getPessoa().setDataNascimento(usuarioForm.getDataNascimento());
        usuario.getPessoa().setTelefone(usuarioForm.getTelefone());
        usuario.getPessoa().setCpf(usuarioForm.getCpf());
        usuario.setEmpresa(empresa);
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

    private UsuarioResponse construirDto(Usuario usuario) {
        return UsuarioMapper.entidadeParaResponse(usuario);
    }
}