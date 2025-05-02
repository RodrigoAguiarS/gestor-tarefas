package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoProduto;
import br.com.rodrigo.gestortarefas.api.model.response.GraficoVenda;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Cacheable("produtos")
    Boolean existsByCodigoBarrasAndAtivo(String codigoBarras, boolean ativo);

    @Cacheable("produtos")
    Boolean existsByCodigoBarrasAndIdNotAndAtivo(String codigoBarras, Long id, boolean ativo);

    @Cacheable("produtos")
    boolean existsByCategoriaId(Long id);

    @Cacheable("produtos")
    @Query("SELECT p FROM Produto p " +
            "JOIN p.categoria c " +
            "WHERE (:id IS NULL OR p.id = :id) " +
            "AND p.ativo = true " +
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

    @Query("SELECT new br.com.rodrigo.gestortarefas.api.model.response.GraficoVenda(p.nome, SUM(iv.quantidade)) " +
            "FROM Produto p " +
            "JOIN ItemVenda iv ON iv.produto.id = p.id " +
            "JOIN Venda v ON v.id = iv.venda.id " +
            "WHERE v.status.id <> :statusCancelado " +
            "GROUP BY p.nome " +
            "ORDER BY SUM(iv.quantidade) DESC")
    List<GraficoVenda> findVendasParaGrafico(@Param("statusCancelado") Long statusCancelado);

    @Query("SELECT new br.com.rodrigo.gestortarefas.api.model.response.GraficoProduto(p.nome, SUM(iv.quantidade * iv.preco)) " +
            "FROM Produto p " +
            "JOIN ItemVenda iv ON iv.produto.id = p.id " +
            "JOIN Venda v ON v.id = iv.venda.id " +
            "WHERE v.status.id <> :statusCancelado " +
            "GROUP BY p.nome " +
            "ORDER BY SUM(iv.quantidade * iv.preco) DESC")
    List<GraficoProduto> findFaturamentoPorProduto(@Param("statusCancelado") Long statusCancelado);

    @Query("SELECT new br.com.rodrigo.gestortarefas.api.model.response.GraficoVenda(c.nome, SUM(iv.quantidade)) " +
            "FROM Produto p " +
            "JOIN p.categoria c " +
            "JOIN ItemVenda iv ON iv.produto.id = p.id " +
            "JOIN Venda v ON v.id = iv.venda.id " +
            "WHERE v.status.id <> :statusCancelado " +
            "GROUP BY c.nome " +
            "ORDER BY SUM(iv.quantidade) DESC")
    List<GraficoVenda> findVendasPorCategoria(@Param("statusCancelado") Long statusCancelado);

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
