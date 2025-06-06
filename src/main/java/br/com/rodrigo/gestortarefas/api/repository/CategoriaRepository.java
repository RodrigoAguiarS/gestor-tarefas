package br.com.rodrigo.gestortarefas.api.repository;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Cacheable("categorias")
    @Query("SELECT c FROM Categoria c " +
            "WHERE (:id IS NULL OR c.id = :id) " +
            "AND (:nome IS NULL OR LOWER(c.nome) LIKE %:nome%) " +
            "AND (:descricao IS NULL OR LOWER(c.descricao) LIKE %:descricao%) ")
    Page<Categoria> findAll(@Param("id") Long id,
                          @Param("nome") String nome,
                          @Param("descricao") String cpf,
                          Pageable pageable);

    @Override
    @CacheEvict(value = "categorias", allEntries = true)
    @NonNull
    <S extends Categoria> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "categorias", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "categorias", allEntries = true)
    void delete(@NonNull Categoria entity);
}
