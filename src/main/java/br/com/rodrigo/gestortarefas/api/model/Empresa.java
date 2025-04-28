package br.com.rodrigo.gestortarefas.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Empresa extends EntidadeBase {

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    private String cnpj;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamento> horariosFuncionamento;

    @OneToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarios;
}
