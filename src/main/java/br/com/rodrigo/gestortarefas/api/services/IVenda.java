package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.form.VendaForm;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface IVenda {
    VendaResponse criar(VendaForm vendaForm, Long id);

    Optional<VendaResponse> consultarPorId(Long id);

    Page<VendaResponse> listarTodos(int page, int size, String sort,
                                    Long id,
                                    String nomeCliente,
                                    Long status,
                                    Long formaPagamento,
                                    BigDecimal valorMinimo,
                                    BigDecimal valorMaximo,
                                    LocalDateTime dataInicio,
                                    LocalDateTime dataFim);

    VendaResponse atualizarStatus(Long id, Long statusId);

}
