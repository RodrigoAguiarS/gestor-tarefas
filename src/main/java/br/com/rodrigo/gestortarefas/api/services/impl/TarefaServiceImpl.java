package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
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
import br.com.rodrigo.gestortarefas.api.services.ITarefa;
import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import br.com.rodrigo.gestortarefas.api.services.SseService;
import br.com.rodrigo.gestortarefas.api.util.MensagemUtil;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;


@Service
@RequiredArgsConstructor
public class TarefaServiceImpl implements ITarefa {

    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final NotificacaoServiceImpl notificacaoServiceImpl;
    private final S3StorageService s3StorageService;
    private final SseService sseService;

    @Override
    public TarefaResponse criar(TarefaForm tarefaForm) {
        Tarefa tarefa = criarEntidade(tarefaForm, null);
        tarefa = tarefaRepository.save(tarefa);
        String mensagem = MensagemUtil.criarMensagemNovaTarefa(tarefa);
        notificacaoServiceImpl.criarNotificacao(tarefa.getResponsavel(), mensagem);
        sseService.notificarUsuario(tarefa.getResponsavel().getId(), mensagem );
        return construirDto(tarefa);
    }

    private Tarefa criarEntidade(TarefaForm form, Tarefa tarefaExistente) {
        Tarefa tarefa = tarefaExistente != null ? tarefaExistente : new Tarefa();
        ModelMapperUtil.map(form, tarefa);
        if (isEmpty(tarefaExistente)) {
            tarefa.setSituacao(Situacao.PENDENTE);
        }
        Usuario responsavel = usuarioRepository.findById(form.getResponsavel())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.USUARIO_NAO_ENCONTRADO_POR_ID.getMessage(form.getResponsavel())));
        tarefa.setDeadline(calcularDeadline(form.getPrioridade()));
        tarefa.setResponsavel(responsavel);
        tarefa.setArquivosUrl(form.getArquivosUrl());
        return tarefa;
    }

    @Override
    public TarefaResponse atualizar(Long id, TarefaForm form) {
        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.TAREFA_NAO_ENCONTRADA_POR_ID.getMessage(id)));

        Usuario antigoResponsavel = tarefaExistente.getResponsavel();
        criarEntidade(form, tarefaExistente);
        tarefaExistente = tarefaRepository.save(tarefaExistente);
        tarefaExistente.setDeadline(calcularDeadline(form.getPrioridade()));
        String mensagem = MensagemUtil.criarMensagemMudancaResponsavel(tarefaExistente);
        notificacaoServiceImpl.criarNotificacao(tarefaExistente.getResponsavel(), mensagem);
        sseService.notificarUsuario(antigoResponsavel.getId(), mensagem );
        return construirDto(tarefaExistente);
    }

    @Override
    public void deletar(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.TAREFA_NAO_ENCONTRADA_POR_ID.getMessage(id)));

        if (isNotEmpty(tarefa.getArquivosUrl())) {
            for (String fileUrl : tarefa.getArquivosUrl()) {
                s3StorageService.apagarArquivo(fileUrl);
            }
        }

        tarefaRepository.delete(tarefa);
    }

    @Override
    @Cacheable("tarefas")
    public Optional<TarefaResponse> consultarPorId(Long id) {
        return tarefaRepository.findById(id).map(this::construirDto);
    }

    @Override
    @Cacheable("tarefas")
    public Page<TarefaResponse> listarTodos(int page, int size, String sort, Long id, String titulo,
                                            String descricao, Situacao situacao, Prioridade prioridade, Long responsavelId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Tarefa> tarefas = tarefaRepository.findAll(id, titulo, descricao, situacao, prioridade, responsavelId, Situacao.CONCLUIDA, pageable);
        return tarefas.map(tarefa -> ModelMapperUtil.map(tarefa, TarefaResponse.class));
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
                        MensagensError.TAREFA_NAO_ENCONTRADA_POR_ID.getMessage(id)));
        tarefa.setSituacao(Situacao.CONCLUIDA);
        String mensagem = MensagemUtil.criarMensagemTarefaConcluida(tarefa);
        notificacaoServiceImpl.criarNotificacao(tarefa.getResponsavel(), mensagem);
        sseService.notificarPorPerfil(mensagem, Perfil.ADMINSTRADOR);
        tarefaRepository.save(tarefa);
    }

    public void andamentoTarefa(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.TAREFA_NAO_ENCONTRADA_POR_ID.getMessage(id)));
        tarefa.setSituacao(Situacao.EM_ANDAMENTO);
        tarefaRepository.save(tarefa);
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
        List<Object[]> resultados = tarefaRepository.findTop10UsuariosComTarefasPorSituacao(pageable);
        return resultados.stream()
                .map(resultado -> new UsuarioComTarefasConcluidasResponse(
                        ModelMapperUtil.map((Usuario) resultado[0], UsuarioResponse.class),
                        (Long) resultado[1],
                        (Long) resultado[2],
                        (Long) resultado[3]
                ))
                .collect(Collectors.toList());
    }

    protected TarefaResponse construirDto(Tarefa tarefa) {
        return ModelMapperUtil.map(tarefa, TarefaResponse.class);
    }

    private LocalDate calcularDeadline(Prioridade prioridade) {
        return switch (prioridade) {
            case ALTA -> LocalDate.now().plusDays(2);
            case MEDIA -> LocalDate.now().plusDays(5);
            case BAIXA -> LocalDate.now().plusDays(7);
        };
    }
}