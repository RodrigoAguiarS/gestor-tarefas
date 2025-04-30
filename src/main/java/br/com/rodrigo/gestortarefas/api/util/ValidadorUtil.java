package br.com.rodrigo.gestortarefas.api.util;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.repository.PessoaRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidadorUtil {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PessoaRepository pessoaRepository;

    public void validarCpfUnico(String cpf, Long idPessoa) {
        if (idPessoa == null) {
            if (pessoaRepository.existsByCpf(cpf)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.CPF_JA_CADASTRADO.getMessage(cpf));
            }
        } else {
            if (pessoaRepository.existsByCpfAndIdNot(cpf, idPessoa)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.CPF_JA_CADASTRADO.getMessage(cpf));
            }
        }
    }

    public void validarEmailUnico(String email, Long idUsuario) {
        if (idUsuario == null) {
            if (usuarioRepository.existsByEmailIgnoreCase(email)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.EMAIL_JA_CADASTRADO.getMessage(email));
            }
        } else {
            if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, idUsuario)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.EMAIL_JA_CADASTRADO.getMessage(email));
            }
        }
    }

    public void validarCodigoBarras(String codigo, Long idProduto) {
        if (idProduto == null) {
            if (produtoRepository.existsByCodigoBarras(codigo)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.CODIGO_BARRAS_DUPLICADO.getMessage(codigo));
            }
        } else {
            if (produtoRepository.existsByCodigoBarrasAndIdNot(codigo, idProduto)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.CODIGO_BARRAS_DUPLICADO.getMessage(codigo));
            }
        }
    }

    public void validarNomeProduto(String nome, Long idProduto) {
        if (idProduto == null) {
            if (produtoRepository.existsByNomeIgnoreCase(nome)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.NOME_PRODUTO_DUPLICADO.getMessage(nome));
            }
        } else {
            if (produtoRepository.existsByNomeIgnoreCaseAndIdNot(nome, idProduto)) {
                throw new ViolacaoIntegridadeDadosException(
                        MensagensError.NOME_PRODUTO_DUPLICADO.getMessage(nome));
            }
        }
    }
}
