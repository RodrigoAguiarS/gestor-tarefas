package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioFuncionamentoResponse {
    private Long id;
    private DayOfWeek diaSemana;
    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
    private Boolean fechado;
}