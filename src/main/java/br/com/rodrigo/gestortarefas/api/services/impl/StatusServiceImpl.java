package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.form.StatusForm;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.repository.StatusRepository;
import br.com.rodrigo.gestortarefas.api.services.IStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements IStatus {

    private final StatusRepository statusRepository;

    @Override
    public StatusResponse criar(StatusForm statusForm) {
        Status status = criaEntidade(statusForm, null);
        status = statusRepository.save(status);
        return construirDto(status);
    }

    private Status criaEntidade(StatusForm statusForm, Long id) {
        Status status  = new Status();
        if (isNotEmpty(id)) {
            status.setId(id);
        }
        status.setNome(statusForm.getNome());
        status.setDescricao(statusForm.getDescricao());

        return status;
    }

    private StatusResponse construirDto(Status status) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setId(status.getId());
        statusResponse.setNome(status.getNome());
        statusResponse.setDescricao(status.getDescricao());
        return statusResponse;
    }

    @Override
    public StatusResponse atualizar(Long id, StatusForm tarefaForm) {
        Status status = criaEntidade(tarefaForm, id);
        status = statusRepository.save(status);
        return construirDto(status);
    }

    @Override
    public void deletar(Long id) {
        statusRepository.deleteById(id);
    }

    @Override
    public Optional<StatusResponse> consultarPorId(Long id) {
        return statusRepository.findById(id).map(this::construirDto);
    }

    @Override
    public List<StatusResponse> buscarPorIds(List<Long> ids) {
        return statusRepository.findAllById(ids)
                .stream()
                .map(this::construirDto)
                .toList();
    }

    @Override
    public Page<StatusResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                               String descricao) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Status> statuss = statusRepository.findAll(id, nome, descricao, pageable);
        return statuss.map(this::construirDto);
    }
}
