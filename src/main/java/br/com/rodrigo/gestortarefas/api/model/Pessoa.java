package br.com.rodrigo.gestortarefas.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Pessoa extends EntidadeBase {

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "telefone", length = 11)
    private String telefone;

    @Column(name = "cpf", unique = true, length = 13)
    private String cpf;

    @Column(name = "data_nascimento")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            usuario.setPessoa(this);
        }
    }
}