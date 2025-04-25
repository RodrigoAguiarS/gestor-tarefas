package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VendaResponse {

    private Long id;
    private ClienteResponse cliente;
    private LocalDateTime dataVenda;
    private List<ItemVendaResponse> itens;
    private String tipoVenda;
    BigDecimal valorTotal;
    StatusResponse status;
    PagamentoResponse pagamento;
}
