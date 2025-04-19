package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface StatusRepository extends JpaRepository<Status, Long> {

    @Cacheable("status")
    @Query("SELECT s FROM Status s " +
            "WHERE (:id IS NULL OR s.id = :id) " +
            "AND (:nome IS NULL OR LOWER(s.nome) LIKE %:nome%) " +
            "AND (:descricao IS NULL OR LOWER(s.descricao) LIKE %:descricao%) ")
    Page<Status> findAll(@Param("id") Long id,
                         @Param("nome") String nome,
                         @Param("descricao") String cpf,
                         Pageable pageable);

    @Override
    @CacheEvict(value = "status", allEntries = true)
    @NonNull
    <S extends Status> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "status", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "status", allEntries = true)
    void delete(@NonNull Status entity);
}
