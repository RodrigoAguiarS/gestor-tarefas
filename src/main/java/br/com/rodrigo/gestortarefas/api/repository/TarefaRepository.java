package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {


    @Query("SELECT t FROM Tarefa t " +
            "JOIN t.responsavel r " +
            "WHERE (:id IS NULL OR t.id = :id) " +
            "AND (:titulo IS NULL OR LOWER(t.titulo) LIKE %:titulo%) " +
            "AND (:descricao IS NULL OR LOWER(t.descricao) LIKE %:descricao%) " +
            "AND (:situacao IS NULL OR t.situacao = :situacao) " +
            "AND (:prioridade IS NULL OR t.prioridade = :prioridade) " +
            "AND (:responsavelId IS NULL OR r.id = :responsavelId) " +
            "AND t.situacao <> 'CONCLUIDA'")
    Page<Tarefa> findAll(@Param("id") Long id,
                         @Param("titulo") String titulo,
                         @Param("descricao") String descricao,
                         @Param("situacao") Situacao situacao,
                         @Param("prioridade") Prioridade prioridade,
                         @Param("responsavelId") Long responsavelId,
                         Pageable pageable);

    List<Tarefa> findAllByResponsavelId(@Param("responsavelId") Long id);

    @Query("SELECT t.situacao, COUNT(t) FROM Tarefa t GROUP BY t.situacao")
    List<Object[]> countTarefasBySituacao();

    @Query("SELECT u, COUNT(t) FROM Usuario u LEFT JOIN Tarefa t ON u.id = t.responsavel.id AND t.situacao = 'CONCLUIDA' GROUP BY u")
    List<Object[]> findUsuariosComTarefasConcluidas();

}
