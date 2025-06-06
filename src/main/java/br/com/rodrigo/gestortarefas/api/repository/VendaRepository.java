package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Venda;
import com.google.firebase.internal.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Cacheable("vendas")
    Page<Venda> findByClienteIdOrderByDataVendaDesc(Long clienteId, Pageable pageable);

    @Cacheable("vendas")
    @Query("""
    SELECT v
    FROM Venda v
    JOIN v.cliente c
    JOIN c.pessoa pe
    JOIN pe.usuario u
    JOIN v.status s
    JOIN v.pagamento p
    WHERE v.id           = COALESCE(:id, v.id)
      AND LOWER(pe.nome) LIKE CONCAT('%', LOWER(COALESCE(:nomeCliente, pe.nome)), '%')
      AND s.id           = COALESCE(:status, s.id)
      AND p.id           = COALESCE(:formaPagamento, p.id)
      AND v.valorTotal  >= COALESCE(:valorMinimo, v.valorTotal)
      AND v.valorTotal  <= COALESCE(:valorMaximo, v.valorTotal)
      AND v.dataVenda   >= COALESCE(:dataInicio, v.dataVenda)
      AND v.dataVenda   <= COALESCE(:dataFim, v.dataVenda)
    ORDER BY v.criadoEm DESC, v.id
""")
    Page<Venda> findAll(
            @Param("id") Long id,
            @Param("nomeCliente") String nomeCliente,
            @Param("status") Long status,
            @Param("formaPagamento") Long formaPagamento,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Override
    @CacheEvict(value = "vendas", allEntries = true)
    @NonNull
    <S extends Venda> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "vendas", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "vendas", allEntries = true)
    void delete(@NonNull Venda entity);
}