package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.form.TarefaForm;
import br.com.rodrigo.gestortarefas.api.model.response.TarefaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioComTarefasConcluidasResponse;
import br.com.rodrigo.gestortarefas.api.services.TarefaServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarefas")
public class TarefaController extends GenericControllerImpl<TarefaForm, TarefaResponse> {

    private final TarefaServiceImpl tarefaServiceImpl;

    protected TarefaController(TarefaServiceImpl tarefaServiceImpl) {
        super(tarefaServiceImpl);
        this.tarefaServiceImpl = tarefaServiceImpl;
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<TarefaResponse>> listarTodos(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) Long id,
                                                            @RequestParam(required = false) String titulo,
                                                            @RequestParam(required = false) String descricao,
                                                            @RequestParam(required = false) Situacao situacao,
                                                            @RequestParam(required = false) Prioridade prioridade,
                                                            @RequestParam(required = false) Long responsavelId) {
        Page<TarefaResponse> tarefas = tarefaServiceImpl.listarTodos(page, size, sort, id, titulo, descricao, situacao, prioridade, responsavelId);
        return ResponseEntity.ok(tarefas);
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirTarefa(@PathVariable Long id) {
        tarefaServiceImpl.concluirTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/andamento")
    public ResponseEntity<Void> andamentoTarefa(@PathVariable Long id) {
        tarefaServiceImpl.andamentoTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contagem-por-situacao")
    public ResponseEntity<Map<Situacao, Long>> contarTarefasPorSituacao() {
        Map<Situacao, Long> contagem = tarefaServiceImpl.contarTarefasPorSituacao();
        return ResponseEntity.ok(contagem);
    }

    @GetMapping("/responsavel/{id}/tarefas")
    public ResponseEntity<List<TarefaResponse>> listarTarefasPorResponsavel(@PathVariable Long id) {
        List<TarefaResponse> tarefas = tarefaServiceImpl.listarTarefasPorResponsavel(id);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/responsavel/tarefas-concluidas")
    public ResponseEntity<List<UsuarioComTarefasConcluidasResponse>> listarUsuariosComTarefasConcluidas() {
        List<UsuarioComTarefasConcluidasResponse> usuarios = tarefaServiceImpl.listarUsuariosComTarefasConcluidas();
        return ResponseEntity.ok(usuarios);
    }
}
