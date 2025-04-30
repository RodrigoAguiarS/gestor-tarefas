package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import com.google.firebase.internal.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Cacheable("clientes")
    Optional <Cliente> findClienteByPessoaId(Long idPessoa);

    @Cacheable("clientes")
    @Query("SELECT c FROM Cliente c " +
            "JOIN c.pessoa p " +
            "JOIN p.usuario u " +
            "JOIN c.endereco e " +
            "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE %:email%) " +
            "AND (:cpf IS NULL OR p.cpf = :cpf) " +
            "AND (:cidade IS NULL OR LOWER(e.cidade) LIKE %:cidade%) " +
            "AND (:estado IS NULL OR LOWER(e.estado) LIKE %:estado%) " +
            "AND (:cep IS NULL OR e.cep = :cep) " +
            "AND c.ativo = true " +
            "AND p.ativo = true " +
            "AND u.ativo = true " +
            "ORDER BY p.nome ASC")
    Page<Cliente> findAll(@Param("email") String email,
                          @Param("nome") String nome,
                          @Param("cpf") String cpf,
                          @Param("cidade") String cidade,
                          @Param("estado") String estado,
                          @Param("cep") String cep,
                          Pageable pageable);

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    @NonNull
    <S extends Cliente> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    void delete(@NonNull Cliente entity);
}


