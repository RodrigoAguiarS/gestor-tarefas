package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.Notificacao;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;


public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    @Cacheable("notificacoes")
    Page<Notificacao> findByUsuarioAndLidaFalse(Usuario usuario, Pageable pageable);

    @Override
    @CacheEvict(value = "notificacoes", allEntries = true)
    @NonNull
    <S extends Notificacao> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "notificacoes", allEntries = true)
    void deleteById(@NonNull Long id);

    @Override
    @CacheEvict(value = "notificacoes", allEntries = true)
    void delete(@NonNull Notificacao entity);
}
