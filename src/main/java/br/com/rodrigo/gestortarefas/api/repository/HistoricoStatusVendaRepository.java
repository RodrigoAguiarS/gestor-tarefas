package br.com.rodrigo.gestortarefas.api.repository;


import br.com.rodrigo.gestortarefas.api.model.HistoricoStatusVenda;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface HistoricoStatusVendaRepository extends JpaRepository<HistoricoStatusVenda, Long> {

    @Cacheable("vendas")
    List<HistoricoStatusVenda> findByVendaIdOrderByCriadoEmAsc(Long vendaId);
}
