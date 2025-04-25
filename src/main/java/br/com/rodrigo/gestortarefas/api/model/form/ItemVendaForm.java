package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemVendaForm {
    @NotNull
    private ProdutoForm produto;

    @NotNull
    @Min(1)
    private Integer quantidade;
}
