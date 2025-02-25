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

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Tarefa extends EntidadeBase {

    @Column(name = "titulo", length = 100)
    private String titulo;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario responsavel;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Situacao situacao;
}
