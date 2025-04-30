package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private Long id;
    private String email;
    private PessoaResponse pessoa;
    private EmpresaResponse empresa;
    private Set<PerfilResponse> perfis;
    private Boolean ativo;
}
