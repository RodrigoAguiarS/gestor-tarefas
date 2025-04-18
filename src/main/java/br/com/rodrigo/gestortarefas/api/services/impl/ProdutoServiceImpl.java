package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.ICategoria;
import br.com.rodrigo.gestortarefas.api.services.IProduto;
import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements IProduto {

    private final ICategoria categoriaService;
    private final ProdutoRepository produtoRepository;
    private final S3StorageService s3StorageService;

    @Override
    public ProdutoResponse criar(ProdutoForm produtoForm) {
        Produto produto = criaProduto(produtoForm, null);
        produto = produtoRepository.save(produto);
        return construirDto(produto);
    }

    @Override
    public ProdutoResponse atualizar(Long id, ProdutoForm produtoForm) {
        Produto produto = criaProduto(produtoForm, id);
        produto = produtoRepository.save(produto);
        return construirDto(produto);
    }

    @Override
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(id)));

        if (isNotEmpty(produto.getArquivosUrl())) {
            for (String fileUrl : produto.getArquivosUrl()) {
                s3StorageService.apagarArquivo(fileUrl);
            }
        }

        produtoRepository.delete(produto);
    }

    @Override
    public Optional<ProdutoResponse> consultarPorId(Long id) {
        return produtoRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<ProdutoResponse> listarTodos(int page, int size, String sort, Long id, String nome, String descricao,
                                             BigDecimal preco, String codigoBarras, Integer quantidade, Long categoriaId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Produto> produtos = produtoRepository.findAll(id, nome, descricao, preco, codigoBarras, quantidade, categoriaId, pageable);
        return produtos.map(this::construirDto);
    }

    private Produto criaProduto(ProdutoForm produtoForm, Long id) {
        Produto produto = id == null
                ? new Produto()
                : produtoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(id)));
        validarCodigoBarras(produtoForm.getCodigoBarras(), id);

        CategoriaResponse response = categoriaService.consultarPorId(produtoForm.getCategoriaId())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CATEGORIA_NAO_ENCONTRADA.getMessage(produtoForm.getCategoriaId())));

        Categoria categoria = new Categoria();
        categoria.setId(response.getId());
        categoria.setNome(response.getNome());
        categoria.setDescricao(response.getDescricao());

        produto.setNome(produtoForm.getNome());
        produto.setPreco(produtoForm.getPreco());
        produto.setDescricao(produtoForm.getDescricao());
        produto.setCodigoBarras(produtoForm.getCodigoBarras());
        produto.setQuantidade(produtoForm.getQuantidade());
        produto.setArquivosUrl(produtoForm.getArquivosUrl());
        produto.setCategoria(categoria);

        return produto;
    }

    private ProdutoResponse construirDto(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getQuantidade(),
                produto.getPreco(),
                new CategoriaResponse(
                        produto.getCategoria().getId(),
                        produto.getCategoria().getNome(),
                        produto.getCategoria().getDescricao()),
                produto.getArquivosUrl());

    }

    private void validarCodigoBarras(String codigoBarras, Long id) {
        boolean codigoBarrasExiste;
        if (id == null) {
            codigoBarrasExiste = produtoRepository.existsByCodigoBarras(codigoBarras);
        } else {
            codigoBarrasExiste = produtoRepository.existsByCodigoBarrasAndIdNot(codigoBarras, id);
        }
        if (codigoBarrasExiste) {
            throw new ViolacaoIntegridadeDadosException(
                    MensagensError.CODIGO_BARRAS_DUPLICADO.getMessage(codigoBarras));
        }
    }
}
