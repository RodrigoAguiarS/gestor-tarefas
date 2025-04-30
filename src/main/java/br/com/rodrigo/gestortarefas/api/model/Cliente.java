package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Cliente extends EntidadeBase {

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco = new Endereco();
}
