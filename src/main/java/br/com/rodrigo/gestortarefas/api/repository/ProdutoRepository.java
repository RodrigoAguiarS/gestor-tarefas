package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByCategoriaId(Long id);
}
