package br.com.rodrigo.gestortarefas.api.model.form;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TarefaForm {

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @NotBlank
    @Size(max = 500)
    private String descricao;

    @NotNull
    private Long responsavel;

    @NotNull
    private Prioridade prioridade;

    private List<String> arquivosUrl;
}
