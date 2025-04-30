package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
}
