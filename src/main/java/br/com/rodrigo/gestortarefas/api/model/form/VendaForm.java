package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendaForm {
    @NotNull
    private Long cliente;

    @NotNull
    private Long pagamento;

    @NotEmpty
    @Valid
    private List<ItemVendaForm> itens;

    private String tipoVenda;

    BigDecimal valorTotal;
}
