package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class MovimentacaoEstoque  extends EntidadeBase {

    private int quantidade;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    private OrigemMovimentacao origem;

    @Enumerated(EnumType.STRING)
    private AcaoMovimentacao acao;

    private Long idOrigem;

    private LocalDateTime dataHora = LocalDateTime.now();
}
