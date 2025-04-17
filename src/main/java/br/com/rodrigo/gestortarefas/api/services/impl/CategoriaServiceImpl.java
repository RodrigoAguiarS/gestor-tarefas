package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import br.com.rodrigo.gestortarefas.api.model.form.CategoriaForm;
import br.com.rodrigo.gestortarefas.api.model.response.CategoriaResponse;
import br.com.rodrigo.gestortarefas.api.repository.CategoriaRepository;
import br.com.rodrigo.gestortarefas.api.repository.ProdutoRepository;
import br.com.rodrigo.gestortarefas.api.services.ICategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoria {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    @Override
    public CategoriaResponse criar(CategoriaForm categoriaForm) {
        Categoria categoria = criaEntidade(categoriaForm, null);
        categoria = categoriaRepository.save(categoria);
        return construirDto(categoria);
    }

    private Categoria criaEntidade(CategoriaForm categoriaForm, Long id) {
        Categoria categoria  = new Categoria();
        if (isNotEmpty(id)) {
            categoria.setId(id);
        }
        categoria.setNome(categoriaForm.getNome());
        categoria.setDescricao(categoriaForm.getDescricao());

        return categoria;
    }

    private CategoriaResponse construirDto(Categoria categoria) {
        CategoriaResponse categoriaResponse = new CategoriaResponse();
        categoriaResponse.setId(categoria.getId());
        categoriaResponse.setNome(categoria.getNome());
        categoriaResponse.setDescricao(categoria.getDescricao());
        return categoriaResponse;
    }

    @Override
    public CategoriaResponse atualizar(Long id, CategoriaForm tarefaForm) {
        Categoria categoria = criaEntidade(tarefaForm, id);
        categoria = categoriaRepository.save(categoria);
        return construirDto(categoria);
    }

    @Override
    public void deletar(Long id) {
        validarExclusao(id);
        categoriaRepository.deleteById(id);
    }

    @Override
    public Optional<CategoriaResponse> consultarPorId(Long id) {
        return categoriaRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<CategoriaResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                               String descricao) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Categoria> categorias = categoriaRepository.findAll(id, nome, descricao, pageable);
        return categorias.map(this::construirDto);
    }

    private void validarExclusao(Long id) {
        boolean existeCategoria = produtoRepository.existsByCategoriaId(id);
        if (existeCategoria) {
            throw new ViolacaoIntegridadeDadosException(MensagensError.CATEGORIA_POSSUI_PRODUTO.getMessage());
        }
    }
}
