package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.Usuario;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Cacheable("usuarios")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @Cacheable("usuarios")
    Boolean existsByEmailIgnoreCase(String email);

    @Cacheable("usuarios")
    Boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Cacheable("usuarios")
    Boolean existsByPessoaCpf(String cpf);

    @Cacheable("usuarios")
    Boolean existsByPessoaCpfAndIdNot(String cpf, Long id);

    @Cacheable("usuarios")
    boolean existsByPerfisId(Long idPerfil);

    @Cacheable("usuarios")
    @Query("SELECT u FROM Usuario u " +
            "JOIN u.pessoa p " +
            "JOIN u.perfis pf " +
            "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE %:email%) " +
            "AND (:cpf IS NULL OR p.cpf = :cpf) " +
            "AND (:perfilId IS NULL OR pf.id = :perfilId)")
    Page<Usuario> findAll(@Param("email") String email,
                          @Param("nome") String nome,
                          @Param("cpf") String cpf,
                          @Param("perfilId") Long perfilId,
                          Pageable pageable);

    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    @NonNull
    <S extends Usuario> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "usuarios", allEntries = true)
    void delete(@NonNull Usuario entity);

    @Cacheable("usuarios")
    List<Usuario> findAllByPerfisId(long idPerfil);
}
