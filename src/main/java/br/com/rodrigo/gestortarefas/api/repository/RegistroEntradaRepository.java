package br.com.rodrigo.gestortarefas.api.repository;

import br.com.rodrigo.gestortarefas.api.model.RegistroEntrada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroEntradaRepository extends JpaRepository<RegistroEntrada, Long> {

    RegistroEntrada findTopByUsuarioIdOrderByDataEntradaDesc(Long idUsuario);
}
