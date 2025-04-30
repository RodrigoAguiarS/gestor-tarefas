package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Funcionario;
import com.google.firebase.internal.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Cacheable("funcionarios")
    Optional <Funcionario> findFuncionarioByPessoaId(Long idPessoa);

    @Cacheable("funcionarios")
    @Query("SELECT f FROM Funcionario f " +
            "JOIN f.pessoa p " +
            "JOIN p.usuario u " +
            "JOIN u.perfis pf " +
            "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE %:email%) " +
            "AND (:cpf IS NULL OR p.cpf = :cpf) " +
            "AND (:cargo IS NULL OR LOWER(f.cargo) LIKE %:cargo%) " +
            "AND (:matricula IS NULL OR LOWER(f.matricula) LIKE %:matricula%) " +
            "AND (:perfilId IS NULL OR pf.id = :perfilId)" +
            "AND f.ativo = true " +
            "AND p.ativo = true " +
            "AND u.ativo = true " +
            "ORDER BY p.nome ASC")
    Page<Funcionario> findAll(@Param("email") String email,
                          @Param("nome") String nome,
                          @Param("cpf") String cpf,
                          @Param("cargo") String cargo,
                          @Param("matricula") String matricula,
                          @Param("perfilId") Long perfilId,
                          Pageable pageable);

    @Override
    @CacheEvict(value = "funcionarios", allEntries = true)
    @NonNull
    <S extends Funcionario> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "funcionarios", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "funcionarios", allEntries = true)
    void delete(@NonNull Funcionario entity);
}


