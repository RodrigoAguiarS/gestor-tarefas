package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Boolean existsByCodigoBarras(String codigoBarras);

    Boolean existsByCodigoBarrasAndIdNot(String codigoBarras, Long id);

    boolean existsByCategoriaId(Long id);

    @Query("SELECT p FROM Produto p " +
            "JOIN p.categoria c " +
            "WHERE (:id IS NULL OR p.id = :id) " +
            "AND (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:descricao IS NULL OR LOWER(p.descricao) LIKE %:descricao%) " +
            "AND (:preco IS NULL OR p.preco = :preco) " +
            "AND (:codigoBarras IS NULL OR p.codigoBarras = :codigoBarras) " +
            "AND (:quantidade IS NULL OR p.quantidade = :quantidade) " +
            "AND (:categoriaId IS NULL OR c.id = :categoriaId) " +
            "ORDER BY p.criadoEm DESC")
    Page<Produto> findAll(@Param("id") Long id,
                          @Param("nome") String nome,
                          @Param("descricao") String descricao,
                          @Param("preco") BigDecimal preco,
                          @Param("codigoBarras") String codigoBarras,
                          @Param("quantidade") Integer quantidade,
                          @Param("categoriaId") Long categoriaId,
                          Pageable pageable);
}
