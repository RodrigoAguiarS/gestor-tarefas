package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {


    @Cacheable("tarefas")
    @Query("SELECT t FROM Tarefa t " +
            "JOIN t.responsavel r " +
            "WHERE (:id IS NULL OR t.id = :id) " +
            "AND (:titulo IS NULL OR LOWER(t.titulo) LIKE %:titulo%) " +
            "AND (:descricao IS NULL OR LOWER(t.descricao) LIKE %:descricao%) " +
            "AND (:situacao IS NULL OR t.situacao = :situacao) " +
            "AND (:prioridade IS NULL OR t.prioridade = :prioridade) " +
            "AND (:responsavelId IS NULL OR r.id = :responsavelId) " +
            "AND t.situacao <> :situacaoConcluida " +
            "ORDER BY t.criadoEm DESC")
    Page<Tarefa> findAll(@Param("id") Long id,
                         @Param("titulo") String titulo,
                         @Param("descricao") String descricao,
                         @Param("situacao") Situacao situacao,
                         @Param("prioridade") Prioridade prioridade,
                         @Param("responsavelId") Long responsavelId,
                         @Param("situacaoConcluida") Situacao situacaoConcluida,
                         Pageable pageable);

    @Cacheable("tarefas")
    List<Tarefa> findAllByResponsavelId(@Param("responsavelId") Long id);

    @Cacheable("tarefas")
    @Query("SELECT t.situacao, COUNT(t) FROM Tarefa t GROUP BY t.situacao")
    List<Object[]> countTarefasBySituacao();

    @Cacheable("tarefas")
    @Query("SELECT u, " +
            "SUM(CASE WHEN t.situacao = 'CONCLUIDA' THEN 1 ELSE 0 END) AS concluidas, " +
            "SUM(CASE WHEN t.situacao = 'PENDENTE' THEN 1 ELSE 0 END) AS pendentes, " +
            "SUM(CASE WHEN t.situacao = 'EM_ANDAMENTO' THEN 1 ELSE 0 END) AS emAndamento " +
            "FROM Usuario u LEFT JOIN Tarefa t ON u.id = t.responsavel.id " +
            "GROUP BY u ORDER BY concluidas DESC")
    List<Object[]> findTop10UsuariosComTarefasPorSituacao(Pageable pageable);

    @Cacheable("tarefas")
    boolean existsByResponsavelId(Long usuarioId);

    @Override
    @CacheEvict(value = "tarefas", allEntries = true)
    @NonNull
    <S extends Tarefa> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "tarefas", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "tarefas", allEntries = true)
    void delete(@NonNull Tarefa entity);
}
