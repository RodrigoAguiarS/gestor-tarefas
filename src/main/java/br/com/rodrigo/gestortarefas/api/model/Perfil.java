package br.com.rodrigo.gestortarefas.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Perfil extends EntidadeBase {

    public final static Long ADMINSTRADOR = 1L;
    public final static Long OPERADOR = 2L;
    public final static Long CLIENTE = 3L;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;
}
