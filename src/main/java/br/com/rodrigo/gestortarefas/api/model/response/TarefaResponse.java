package br.com.rodrigo.gestortarefas.api.model.response;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TarefaResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private UsuarioResponse responsavel;
    private Prioridade prioridade;
    private LocalDate deadline;
    private Situacao situacao;
    private List<String> arquivosUrl;
}
