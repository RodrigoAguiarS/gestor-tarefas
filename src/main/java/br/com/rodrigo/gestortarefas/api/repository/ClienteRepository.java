package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c " +
            "JOIN c.usuario u " +
            "JOIN u.pessoa p " +
            "JOIN c.endereco e " +
            "WHERE (:nome IS NULL OR LOWER(p.nome) LIKE %:nome%) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE %:email%) " +
            "AND (:cpf IS NULL OR p.cpf = :cpf) " +
            "AND (:cidade IS NULL OR LOWER(e.cidade) LIKE %:cidade%) " +
            "AND (:estado IS NULL OR LOWER(e.estado) LIKE %:estado%) " +
            "AND (:cep IS NULL OR e.cep = :cep) " +
            "ORDER BY p.nome ASC")
    Page<Cliente> findAll(@Param("email") String email,
                          @Param("nome") String nome,
                          @Param("cpf") String cpf,
                          @Param("cidade") String cidade,
                          @Param("estado") String estado,
                          @Param("cep") String cep,
                          Pageable pageable);
}
