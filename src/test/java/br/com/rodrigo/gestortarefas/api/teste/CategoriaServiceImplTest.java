package br.com.rodrigo.gestortarefas.api.teste;

import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.form.CategoriaForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.repository.CategoriaRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.impl.CategoriaServiceImpl;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private CategoriaForm categoriaForm;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoriaForm = new CategoriaForm();
        categoriaForm.setNome("Categoria Teste");
        categoriaForm.setDescricao("Descrição Teste");

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Categoria Teste");
        categoria.setDescricao("Descrição Teste");

        CategoriaResponse categoriaResponse = new CategoriaResponse();
        categoriaResponse.setId(1L);
        categoriaResponse.setNome("Categoria Teste");
        categoriaResponse.setDescricao("Descrição Teste");
    }

    @Test
    void criar_DeveRetornarCategoriaCriada() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponse resultado = categoriaService.criar(categoriaForm);

        assertNotNull(resultado);
        assertEquals(categoria.getId(), resultado.getId());
        assertEquals(categoria.getNome(), resultado.getNome());
        assertEquals(categoria.getDescricao(), resultado.getDescricao());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void atualizar_DeveRetornarCategoriaAtualizada() {
        Long id = 1L;
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponse resultado = categoriaService.atualizar(id, categoriaForm);

        assertNotNull(resultado);
        assertEquals(categoria.getId(), resultado.getId());
        assertEquals(categoria.getNome(), resultado.getNome());
        assertEquals(categoria.getDescricao(), resultado.getDescricao());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void deletar_QuandoCategoriaNaoTemProdutos_DeveExcluirCategoria() {
        Long id = 1L;
        when(produtoRepository.existsByCategoriaId(id)).thenReturn(false);

        assertDoesNotThrow(() -> categoriaService.deletar(id));

        verify(categoriaRepository, times(1)).deleteById(id);
        verify(produtoRepository, times(1)).existsByCategoriaId(id);
    }

    @Test
    void deletar_QuandoCategoriaTemProdutos_DeveLancarExcecao() {
        Long id = 1L;
        when(produtoRepository.existsByCategoriaId(id)).thenReturn(true);

        assertThrows(ViolacaoIntegridadeDadosException.class,
                () -> categoriaService.deletar(id));
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void consultarPorId_QuandoCategoriaExiste_DeveRetornarCategoria() {
        Long id = 1L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

        Optional<CategoriaResponse> resultado = categoriaService.consultarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(categoria.getId(), resultado.get().getId());
        assertEquals(categoria.getNome(), resultado.get().getNome());
        assertEquals(categoria.getDescricao(), resultado.get().getDescricao());
    }

    @Test
    void consultarPorId_QuandoCategoriaNaoExiste_DeveRetornarVazio() {
        Long id = 1L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CategoriaResponse> resultado = categoriaService.consultarPorId(id);

        assertFalse(resultado.isPresent());
    }

    @Test
    void listarTodos_DeveRetornarPaginaComCategorias() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> categoriasPage = new PageImpl<>(
                Collections.singletonList(categoria),
                pageable,
                1
        );
        when(categoriaRepository.findAll(pageable)).thenReturn(categoriasPage);

        Page<CategoriaResponse> resultado = categoriaService.listarTodos(pageable);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getContent().size());
        assertEquals(categoria.getId(), resultado.getContent().get(0).getId());
        assertEquals(categoria.getNome(), resultado.getContent().get(0).getNome());
        assertEquals(categoria.getDescricao(), resultado.getContent().get(0).getDescricao());
    }
}