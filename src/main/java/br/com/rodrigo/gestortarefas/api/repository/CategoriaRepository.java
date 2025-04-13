package br.com.rodrigo.gestortarefas.api.repository;
import br.com.rodrigo.gestortarefas.api.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
