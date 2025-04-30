package br.com.rodrigo.gestortarefas.api.model.response;

import br.com.rodrigo.gestortarefas.api.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmpresaResponse {
    private Long id;
    private String nome;
    private String cnpj;
    private Endereco endereco;
    private List<HorarioFuncionamentoResponse> horariosFuncionamento;
    private String telefone;
}
