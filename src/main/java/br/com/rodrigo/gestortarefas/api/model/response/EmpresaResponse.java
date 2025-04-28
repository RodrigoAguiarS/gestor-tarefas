package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaResponse {
    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String horarioAtendimento;
}
