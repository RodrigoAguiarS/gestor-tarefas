package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Empresa;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Cacheable("empresas")
    @Query("SELECT e FROM Empresa e " +
            "WHERE (:nome IS NULL OR LOWER(e.nome) LIKE %:nome%) " +
            "AND (:cnpj IS NULL OR e.cnpj = :cnpj) " +
            "AND (:telefone IS NULL OR e.telefone LIKE %:telefone%)")
    Page<Empresa> findAll(@Param("nome") String nome,
                          @Param("cnpj") String cnpj,
                          @Param("telefone") String telefone,
                          Pageable pageable);

}