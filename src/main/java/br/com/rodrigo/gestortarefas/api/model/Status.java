package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Status extends EntidadeBase{
    public final static Long EM_ADAMENTO = 1L;
    public final static Long ESTORNADO = 4L;
    public final static Long CONCLUIDO = 2L;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;
}
