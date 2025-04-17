package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.form.ProdutoForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.ProdutoResponse;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.ICategoria;
import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import br.com.rodrigo.gestortarefas.api.services.impl.ProdutoServiceImpl;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ICategoria categoriaService;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private ProdutoForm produtoForm;
    private Produto produto;
    private CategoriaResponse categoriaResponse;

    @BeforeEach
    void setUp() {
        produtoForm = new ProdutoForm();
        produtoForm.setNome("Produto Teste");
        produtoForm.setDescricao("Descrição Teste");
        produtoForm.setCodigoBarras("123456789");
        produtoForm.setPreco(BigDecimal.valueOf(100.00));
        produtoForm.setQuantidade(10);
        produtoForm.setCategoria(1L);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição Categoria");

        categoriaResponse = new CategoriaResponse();
        categoriaResponse.setId(1L);
        categoriaResponse.setNome("Categoria Teste");
        categoriaResponse.setDescricao("Descrição Categoria");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição Teste");
        produto.setCodigoBarras("123456789");
        produto.setPreco(BigDecimal.valueOf(100.00));
        produto.setQuantidade(10);
        produto.setCategoria(categoria);
    }

    @Test
    void criar_DeveRetornarProdutoCriado() {
        when(categoriaService.consultarPorId(any())).thenReturn(Optional.of(categoriaResponse));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(produtoRepository.existsByCodigoBarras(any())).thenReturn(false);

        ProdutoResponse resultado = produtoService.criar(produtoForm);

        assertNotNull(resultado);
        assertEquals(produto.getId(), resultado.getId());
        assertEquals(produto.getNome(), resultado.getNome());
        assertEquals(produto.getCodigoBarras(), resultado.getCodigoBarras());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void criar_QuandoCodigoBarrasJaExiste_DeveLancarExcecao() {
        when(produtoRepository.existsByCodigoBarras(any())).thenReturn(true);

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> produtoService.criar(produtoForm));
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void atualizar_DeveRetornarProdutoAtualizado() {
        Long id = 1L;
        when(categoriaService.consultarPorId(any())).thenReturn(Optional.of(categoriaResponse));
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(produtoRepository.existsByCodigoBarrasAndIdNot(any(), any())).thenReturn(false);

        ProdutoResponse resultado = produtoService.atualizar(id, produtoForm);

        assertNotNull(resultado);
        assertEquals(produto.getId(), resultado.getId());
        assertEquals(produto.getNome(), resultado.getNome());
        assertEquals(produto.getCodigoBarras(), resultado.getCodigoBarras());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deletar_QuandoProdutoExiste_DeveDeletarProduto() {
        Long id = 1L;
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        assertDoesNotThrow(() -> produtoService.deletar(id));

        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    void deletar_QuandoProdutoNaoExiste_DeveLancarExcecao() {
        Long id = 1L;
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetoNaoEncontradoException.class,
                () -> produtoService.deletar(id));
        verify(produtoRepository, never()).delete(any());
    }

    @Test
    void consultarPorId_QuandoProdutoExiste_DeveRetornarProduto() {
        Long id = 1L;
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        Optional<ProdutoResponse> resultado = produtoService.consultarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(produto.getId(), resultado.get().getId());
        assertEquals(produto.getNome(), resultado.get().getNome());
        assertEquals(produto.getCodigoBarras(), resultado.get().getCodigoBarras());
    }

    @Test
    void listarTodos_DeveRetornarPaginaComProdutos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> produtosPage = new PageImpl<>(
                Collections.singletonList(produto),
                pageable,
                1
        );
        when(produtoRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(produtosPage);

        Page<ProdutoResponse> resultado = produtoService.listarTodos(
                0, 10, "id", null, null, null,
                null, null, null, null);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(produto.getId(), resultado.getContent().get(0).getId());
        assertEquals(produto.getNome(), resultado.getContent().get(0).getNome());
        assertEquals(produto.getCodigoBarras(), resultado.getContent().get(0).getCodigoBarras());
    }
}