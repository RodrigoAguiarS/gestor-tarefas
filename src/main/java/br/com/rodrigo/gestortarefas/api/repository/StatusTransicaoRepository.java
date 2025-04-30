package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.StatusTransicao;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusTransicaoRepository extends JpaRepository<StatusTransicao, Long> {
    List<StatusTransicao> findByStatusAtualIdAndTipoVenda(Long statusAtualId, TipoVenda tipoVenda);
}