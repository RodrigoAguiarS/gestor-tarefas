package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.HorarioFuncionamento;
import br.com.rodrigo.gestortarefas.api.model.response.HorarioFuncionamentoResponse;

import java.util.List;

public class HorarioFuncionamentoMapper {

    public static HorarioFuncionamentoResponse entidadeParaResponse(HorarioFuncionamento horarioFuncionamento) {
        return HorarioFuncionamentoResponse.builder()
                .id(horarioFuncionamento.getId())
                .diaSemana(horarioFuncionamento.getDiaSemana())
                .horaAbertura(horarioFuncionamento.getHoraAbertura())
                .horaFechamento(horarioFuncionamento.getHoraFechamento())
                .fechado(horarioFuncionamento.isFechado())
                .build();
    }

    public static List<HorarioFuncionamentoResponse> listaEntidadeParaResponse(List<HorarioFuncionamento> horarios) {
        if (horarios == null) return null;
        return horarios.stream()
                .map(HorarioFuncionamentoMapper::entidadeParaResponse)
                .toList();
    }
}
