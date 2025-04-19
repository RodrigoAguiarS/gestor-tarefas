package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusForm {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 500)
    private String descricao;
}
