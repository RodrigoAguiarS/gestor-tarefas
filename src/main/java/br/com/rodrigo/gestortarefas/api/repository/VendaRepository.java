package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Venda;
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

    @Query("""
    SELECT v
    FROM Venda v
    JOIN v.cliente c
    JOIN c.usuario u
    JOIN u.pessoa pe
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
}