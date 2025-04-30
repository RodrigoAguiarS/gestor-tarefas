package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerfilResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
}
