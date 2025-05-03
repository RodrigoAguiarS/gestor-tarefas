package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.UsuarioMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Funcionario;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Pessoa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.FuncionarioForm;
import br.com.rodrigo.gestortarefas.api.model.response.FuncionarioResponse;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.FuncionarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IFuncionario;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.services.IUsuario;
import br.com.rodrigo.gestortarefas.api.util.MatriculaGeneratorUtil;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioServiceImpl implements IFuncionario {

    private final FuncionarioRepository funcionarioRepository;
    private final IPerfil perfilService;
    private final IUsuario usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final ValidadorUtil validadorUtil;

    @Override
    public FuncionarioResponse criar(Long idCliente, FuncionarioForm funcionarioForm) {
        Funcionario funcionario = criarFuncionario(funcionarioForm, idCliente);
        validadorUtil.validarCpfUnico(funcionarioForm.getCpf(), funcionario.getId());
        validadorUtil.validarEmailUnico(funcionarioForm.getEmail(), funcionario.getPessoa().getUsuario().getId());
        funcionario = funcionarioRepository.save(funcionario);
        return construirDto(funcionario);
    }

    @Override
    public void deletar(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(id)));
        funcionario.desativar();
        funcionario.getPessoa().desativar();
        funcionario.getPessoa().getUsuario().desativar();
        funcionarioRepository.save(funcionario);
    }

    @Override
    public Optional<FuncionarioResponse> consultarPorId(Long idCliente) {
        return funcionarioRepository.findById(idCliente)
                .map(this::construirDto);
    }

    @Override
    public Page<FuncionarioResponse> buscar(int page, int size, String sort, String email, String nome, String cpf,
                                            String cargo, String matricula, Long perfilId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Funcionario> funcionarios = funcionarioRepository.findAll(email, nome, cpf, cargo, matricula, perfilId, pageable);
        return funcionarios.map(this::construirDto);
    }

    private Funcionario criarFuncionario(FuncionarioForm funcionarioForm, Long idFuncionario) {
        Funcionario funcionario = idFuncionario != null ? funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(idFuncionario))) : new Funcionario();

        PerfilResponse perfilResponse = perfilService.consultarPorId(funcionarioForm.getPerfil())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PERFIL_NAO_ENCONTRADO.getMessage(funcionarioForm.getPerfil())));

        UsuarioResponse usuarioResponse = usuarioService.obterUsuarioLogado();

        Empresa empresaFuncionario = Empresa.builder()
                .id(usuarioResponse.getEmpresa().getId())
                .nome(usuarioResponse.getEmpresa().getNome())
                .cnpj(usuarioResponse.getEmpresa().getCnpj())
                .telefone(usuarioResponse.getEmpresa().getTelefone())
                .endereco(usuarioResponse.getEmpresa().getEndereco())
                .build();

        Perfil perfilFuncionario = Perfil.builder()
                .id(perfilResponse.getId())
                .nome(perfilResponse.getNome())
                .descricao(perfilResponse.getDescricao())
                .build();

        Usuario usuario = funcionario.getPessoa() != null && funcionario.getPessoa().getUsuario() != null ?
                funcionario.getPessoa().getUsuario() : new Usuario();
        usuario.setEmail(funcionarioForm.getEmail());
        if (funcionarioForm.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(funcionarioForm.getSenha()));
        }
        usuario.setPerfis(Set.of(perfilFuncionario));
        usuario.setEmpresa(empresaFuncionario);

        Pessoa pessoa = funcionario.getPessoa() != null ? funcionario.getPessoa() : new Pessoa();
        pessoa.setNome(funcionarioForm.getNome());
        pessoa.setCpf(funcionarioForm.getCpf());
        pessoa.setDataNascimento(funcionarioForm.getDataNascimento());
        pessoa.setTelefone(funcionarioForm.getTelefone());
        pessoa.setUsuario(usuario);

        funcionario.setCargo(funcionarioForm.getCargo());
        funcionario.setMatricula(MatriculaGeneratorUtil.gerarMatricula());
        funcionario.setSalario(funcionarioForm.getSalario());
        funcionario.setPessoa(pessoa);

        return funcionario;
    }

    private FuncionarioResponse construirDto(Funcionario funcionario) {
        return FuncionarioResponse.builder()
                .id(funcionario.getId())
                .usuario(UsuarioMapper.entidadeParaResponse(funcionario.getPessoa().getUsuario()))
                .cargo(funcionario.getCargo())
                .matricula(funcionario.getMatricula())
                .salario(funcionario.getSalario())
                .perfis(funcionario.getPessoa().getUsuario().getPerfis().stream()
                        .map(perfil -> PerfilResponse.builder()
                                .id(perfil.getId())
                                .nome(perfil.getNome())
                                .descricao(perfil.getDescricao())
                                .ativo(perfil.getAtivo())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
