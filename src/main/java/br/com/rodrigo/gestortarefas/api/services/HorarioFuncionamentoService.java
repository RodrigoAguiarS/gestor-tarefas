package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.HorarioFuncionamento;
import br.com.rodrigo.gestortarefas.api.repository.FeriadoRepository;
import br.com.rodrigo.gestortarefas.api.repository.HorarioFuncionamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HorarioFuncionamentoService {

    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    private final FeriadoRepository feriadoRepository;

    @Cacheable("statusFuncionamento")
    public boolean estaAbertoAgora() {
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek diaAtual = agora.getDayOfWeek();
        LocalTime horaAtual = agora.toLocalTime();
        LocalDate dataAtual = agora.toLocalDate();

        if (feriadoRepository.existsByData(dataAtual)) {
            return false;
        }

        Optional<HorarioFuncionamento> optionalHorario = horarioFuncionamentoRepository.findByDiaSemana(diaAtual);

        if (optionalHorario.isEmpty()) {
            return false;
        }

        HorarioFuncionamento horario = optionalHorario.get();

        if (horario.isFechado()) {
            return false;
        }

        return !horaAtual.isBefore(horario.getHoraAbertura()) && !horaAtual.isAfter(horario.getHoraFechamento());
    }
}
