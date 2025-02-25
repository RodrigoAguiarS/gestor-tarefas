package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.form.TarefaForm;
import br.com.rodrigo.gestortarefas.api.model.response.TarefaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioComTarefasConcluidasResponse;
import br.com.rodrigo.gestortarefas.api.services.TarefaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController extends ControllerBase<TarefaResponse> {

    private final TarefaServiceImpl tarefaServiceImpl;

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@RequestBody @Valid TarefaForm tarefaForm,  UriComponentsBuilder uriBuilder) {
        TarefaResponse response = tarefaServiceImpl.criar(tarefaForm);
        return responderItemCriadoComURI(response, uriBuilder, "/tarefas/{id}", response.getId().toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid TarefaForm tarefaForm) {
        TarefaResponse response = tarefaServiceImpl.atualizar(id, tarefaForm);
        return responderSucessoComItem(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TarefaResponse> deletar(@PathVariable Long id) {
        tarefaServiceImpl.deletar(id);
        return responderSucesso();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponse> consultarPorId(@PathVariable Long id) {
        Optional<TarefaResponse> response = tarefaServiceImpl.consultarPorId(id);
        return response.map(this::responderSucessoComItem)
                .orElseGet(this::responderItemNaoEncontrado);
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
        return responderListaDeItensPaginada(tarefas);
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirTarefa(@PathVariable Long id) {
        tarefaServiceImpl.concluirTarefa(id);
        return responderSemConteudo();
    }

    @PutMapping("/{id}/andamento")
    public ResponseEntity<Void> andamentoTarefa(@PathVariable Long id) {
        tarefaServiceImpl.andamentoTarefa(id);
        return responderSemConteudo();
    }

    @GetMapping("/contagem-por-situacao")
    public ResponseEntity<Map<Situacao, Long>> contarTarefasPorSituacao() {
        Map<Situacao, Long> contagem = tarefaServiceImpl.contarTarefasPorSituacao();
        return ResponseEntity.ok(contagem);
    }

    @GetMapping("/responsavel/{id}/tarefas")
    public ResponseEntity<List<TarefaResponse>> listarTarefasPorResponsavel(@PathVariable Long id) {
        List<TarefaResponse> tarefas = tarefaServiceImpl.listarTarefasPorResponsavel(id);
        return responderListaDeItens(tarefas);
    }

    @GetMapping("/responsavel/tarefas-concluidas")
    public ResponseEntity<List<UsuarioComTarefasConcluidasResponse>> listarUsuariosComTarefasConcluidas() {
        List<UsuarioComTarefasConcluidasResponse> usuarios = tarefaServiceImpl.listarTop10UsuariosComTarefasConcluidas();
        return ResponseEntity.ok(usuarios);
    }
}