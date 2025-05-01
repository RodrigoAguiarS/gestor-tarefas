package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class MovimentacaoEstoque extends EntidadeBase {

    @Column(nullable = false)
    private int quantidade;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    private Integer quantidadeAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemMovimentacao origem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcaoMovimentacao acao;

    @Column(name = "id_origem", nullable = false)
    private Long idOrigem;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}

