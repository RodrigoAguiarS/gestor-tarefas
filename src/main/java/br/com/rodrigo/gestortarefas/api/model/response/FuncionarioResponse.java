package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FuncionarioResponse {
    private Long id;
    private UsuarioResponse usuario;
    private String cargo;
    private String matricula;
    private Double salario;
    private Set<PerfilResponse> perfis;
}
