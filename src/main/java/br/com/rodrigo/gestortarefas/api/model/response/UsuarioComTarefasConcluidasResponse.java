package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioComTarefasConcluidasResponse {

    private UsuarioResponse usuario;
    private Long quantidadeTarefasConcluidas;
    private Long quantidadeTarefasPendentes;
    private Long quantidadeTarefasEmAndamento;

}
