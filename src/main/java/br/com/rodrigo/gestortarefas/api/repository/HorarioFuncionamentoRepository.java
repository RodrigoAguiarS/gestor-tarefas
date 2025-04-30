package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;


@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {
    Optional<HorarioFuncionamento> findByDiaSemana(DayOfWeek diaSemana);
}
