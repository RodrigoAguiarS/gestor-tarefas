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

    public final static Long EM_ADAMENTO = 7L;
    public final static Long PENDENTE = 6L;
    public final static Long ESTORNADO = 3L;
    public final static Long CONCLUIDO = 1L;
    public final static Long CANCELADO = 2L;
    public final static Long EM_PREPARACAO = 8L;
    public final static Long ENVIADO = 4L;
    public final static Long ENTREGUE = 5L;
    public final static Long EM_TRANSPORTE = 9L;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;
}
