package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    Page<Notificacao> findByUsuarioAndLidaFalse(Usuario usuario, Pageable pageable);
}
