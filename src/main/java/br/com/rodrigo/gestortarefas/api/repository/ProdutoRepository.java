package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.Produto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Cacheable("produtos")
    Boolean existsByCodigoBarras(String codigoBarras);

    @Cacheable("produtos")
    Boolean existsByCodigoBarrasAndIdNot(String codigoBarras, Long id);

    @Cacheable("produtos")
    boolean existsByCategoriaId(Long id);

    @Cacheable("produtos")
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

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    @NonNull
    <S extends Produto> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    void delete(@NonNull Produto entity);

    @Cacheable("produtos")
    boolean existsByNomeIgnoreCase(String nome);

    @Cacheable("produtos")
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long idProduto);
}
