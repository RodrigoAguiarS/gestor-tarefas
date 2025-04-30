package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status_transicao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusTransicao extends EntidadeBase {
    @ManyToOne
    @JoinColumn(name = "status_atual_id")
    private Status statusAtual;

    @ManyToOne
    @JoinColumn(name = "proximo_status_id")
    private Status proximoStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVenda tipoVenda;
}
