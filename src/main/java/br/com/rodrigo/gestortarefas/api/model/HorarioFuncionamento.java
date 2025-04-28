package br.com.rodrigo.gestortarefas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class HorarioFuncionamento extends EntidadeBase {

    @Column(name = "hora_abertura", nullable = false)
    private LocalTime horaAbertura;

    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime horaFechamento;

    @Column(name = "dia_semana", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    private boolean fechado;
}
