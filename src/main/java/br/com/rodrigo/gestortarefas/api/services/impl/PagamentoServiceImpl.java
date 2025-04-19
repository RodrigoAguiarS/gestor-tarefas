package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.model.Pagamento;
import br.com.rodrigo.gestortarefas.api.model.form.PagamentoForm;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;
import br.com.rodrigo.gestortarefas.api.repository.PagamentoRepository;
import br.com.rodrigo.gestortarefas.api.services.IPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class PagamentoServiceImpl implements IPagamento {

    private final PagamentoRepository pagamentoRepository;

    @Override
    public PagamentoResponse criar(PagamentoForm pagamentoForm) {
        Pagamento pagamento = criaEntidade(pagamentoForm, null);
        pagamento = pagamentoRepository.save(pagamento);
        return construirDto(pagamento);
    }

    private Pagamento criaEntidade(PagamentoForm pagamentoForm, Long id) {
        Pagamento pagamento  = new Pagamento();
        if (isNotEmpty(id)) {
            pagamento.setId(id);
        }
        pagamento.setNome(pagamentoForm.getNome());
        pagamento.setDescricao(pagamentoForm.getDescricao());
        pagamento.setPorcentagemAcrescimo(pagamentoForm.getPorcentagemAcrescimo());

        return pagamento;
    }

    private PagamentoResponse construirDto(Pagamento pagamento) {
        PagamentoResponse pagamentoResponse = new PagamentoResponse();
        pagamentoResponse.setId(pagamento.getId());
        pagamentoResponse.setNome(pagamento.getNome());
        pagamentoResponse.setDescricao(pagamento.getDescricao());
        pagamentoResponse.setPorcentagemAcrescimo(pagamento.getPorcentagemAcrescimo());
        return pagamentoResponse;
    }

    @Override
    public PagamentoResponse atualizar(Long id, PagamentoForm tarefaForm) {
        Pagamento pagamento = criaEntidade(tarefaForm, id);
        pagamento = pagamentoRepository.save(pagamento);
        return construirDto(pagamento);
    }

    @Override
    public void deletar(Long id) {
        pagamentoRepository.deleteById(id);
    }

    @Override
    public Optional<PagamentoResponse> consultarPorId(Long id) {
        return pagamentoRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<PagamentoResponse> listarTodos(int page, int size, String sort, Long id, String nome,
                                               String descricao, String porcentagemAcrescimo) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "id"));
        Page<Pagamento> pagamentos = pagamentoRepository.findAll(id, nome, descricao, porcentagemAcrescimo, pageable);
        return pagamentos.map(this::construirDto);
    }
}
