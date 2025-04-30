package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    boolean existsByData(LocalDate data);
}
