package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Cacheable("pagamentos")
    @Query("SELECT p FROM Pagamento p " +
            "WHERE (:id IS NULL OR p.id = :id) " +
            "AND (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:porcentagemAcrescimo IS NULL OR p.porcentagemAcrescimo = :porcentagemAcrescimo) " +
            "AND (:descricao IS NULL OR LOWER(p.descricao) LIKE %:descricao%) ")
    Page<Pagamento> findAll(@Param("id") Long id,
                          @Param("nome") String nome,
                          @Param("descricao") String descricao,
                          @Param("porcentagemAcrescimo") String porcentagemAcrescimo,
                          Pageable pageable);

    @Override
    @CacheEvict(value = "pagamentos", allEntries = true)
    @NonNull
    <S extends Pagamento> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "pagamentos", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "pagamentos", allEntries = true)
    void delete(@NonNull Pagamento entity);
}
