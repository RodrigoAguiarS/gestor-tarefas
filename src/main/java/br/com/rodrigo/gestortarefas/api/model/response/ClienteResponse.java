package br.com.rodrigo.gestortarefas.api.model.response;

import br.com.rodrigo.gestortarefas.api.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {
    private Long id;
    private UsuarioResponse usuario;
    private Endereco endereco;
}
