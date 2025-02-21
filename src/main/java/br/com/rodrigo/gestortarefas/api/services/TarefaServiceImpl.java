package br.com.rodrigo.gestortarefas.api.services;


import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Prioridade;
import br.com.rodrigo.gestortarefas.api.model.Situacao;
import br.com.rodrigo.gestortarefas.api.model.Tarefa;
import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.model.form.TarefaForm;
import br.com.rodrigo.gestortarefas.api.model.response.TarefaResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioComTarefasConcluidasResponse;
import br.com.rodrigo.gestortarefas.api.model.response.UsuarioResponse;
import br.com.rodrigo.gestortarefas.api.repository.TarefaRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
public class TarefaServiceImpl extends GenericServiceImpl<Tarefa, TarefaForm, TarefaResponse> {

    private final UsuarioRepository usuarioRepository;

    private final TarefaRepository tarefaRepository;

    protected TarefaServiceImpl(JpaRepository<Tarefa, Long> repository, UsuarioRepository usuarioRepository, TarefaRepository tarefaRepository) {
        super(repository);
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
    }

    @Override
    protected Tarefa criarEntidade(TarefaForm tarefaForm, Long id) {
        Tarefa tarefa = ModelMapperUtil.map(tarefaForm, Tarefa.class);
        if (isNotEmpty(id)) {
            tarefa.setId(id);
        }
        if (isEmpty(tarefa.getResponsavel())) {
            Usuario responsavel = usuarioRepository.findById(tarefaForm.getResponsavel())
                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                            MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(tarefaForm.getResponsavel())));
            tarefa.setResponsavel(responsavel);
            tarefa.setSituacao(Situacao.PENDENTE);
        }
        return tarefa;
    }

    @Override
    public TarefaResponse atualizar(Long id, TarefaForm form) {
        Tarefa tarefaExistente = repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));

        if (isNotEmpty(form.getResponsavel())) {
            Usuario responsavel = usuarioRepository.findById(form.getResponsavel())
                    .orElseThrow(() -> new ObjetoNaoEncontradoException(
                            MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(form.getResponsavel())));
            tarefaExistente.setResponsavel(responsavel);
        }
        ModelMapperUtil.map(form, tarefaExistente);
        tarefaExistente = repository.save(tarefaExistente);
        return construirDto(tarefaExistente);
    }

    public List<TarefaResponse> listarTarefasPorResponsavel(Long id) {
        List<Tarefa> tarefas = tarefaRepository.findAllByResponsavelId(id);
        return tarefas.stream()
                .map(tarefa -> ModelMapperUtil.map(tarefa, TarefaResponse.class))
                .collect(Collectors.toList());
    }

    public void concluirTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));
        tarefa.setSituacao(Situacao.CONCLUIDA);
        tarefaRepository.save(tarefa);
    }

    public void andamentoTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));
        tarefa.setSituacao(Situacao.EM_ANDAMENTO);
        tarefaRepository.save(tarefa);
    }

    public Page<TarefaResponse> listarTodos(int page, int size, String sort, Long id, String titulo,
                                            String descricao, Situacao situacao, Prioridade prioridade, Long responsavelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Tarefa> tarefas = tarefaRepository.findAll(id, titulo, descricao, situacao, prioridade, responsavelId, pageable);
        return tarefas.map(tarefa -> ModelMapperUtil.map(tarefa, TarefaResponse.class));
    }

    public Map<Situacao, Long> contarTarefasPorSituacao() {
        List<Object[]> resultados = tarefaRepository.countTarefasBySituacao();
        return resultados.stream()
                .collect(Collectors.toMap(
                        resultado -> (Situacao) resultado[0],
                        resultado -> (Long) resultado[1]
                ));
    }

    public List<UsuarioComTarefasConcluidasResponse> listarTop10UsuariosComTarefasConcluidas() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Object[]> resultados = tarefaRepository.findTop10UsuariosComTarefasConcluidas(pageable);
        return resultados.stream()
                .map(resultado -> new UsuarioComTarefasConcluidasResponse(
                        ModelMapperUtil.map((Usuario) resultado[0], UsuarioResponse.class),
                        (Long) resultado[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    protected TarefaResponse construirDto(Tarefa tarefa) {
        return ModelMapperUtil.map(tarefa, TarefaResponse.class);
    }

    @Override
    protected void ativar(Tarefa tarefa) {
        tarefa.ativar();
    }

    @Override
    protected void desativar(Tarefa tarefa) {
        tarefa.desativar();
    }
}
