package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.ClienteMapper;
import br.com.rodrigo.gestortarefas.api.conversor.ItemVendaMapper;
import br.com.rodrigo.gestortarefas.api.conversor.PagamentoMapper;
import br.com.rodrigo.gestortarefas.api.conversor.StatusMapper;
import br.com.rodrigo.gestortarefas.api.conversor.VendaMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.HistoricoStatusVenda;
import br.com.rodrigo.gestortarefas.api.model.ItemVenda;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.Produto;
import br.com.rodrigo.gestortarefas.api.model.Status;
import br.com.rodrigo.gestortarefas.api.model.TipoVenda;
import br.com.rodrigo.gestortarefas.api.model.Venda;
import br.com.rodrigo.gestortarefas.api.model.form.VendaForm;
import br.com.rodrigo.gestortarefas.api.model.response.ClienteResponse;
import br.com.rodrigo.gestortarefas.api.model.response.PagamentoResponse;
import br.com.rodrigo.gestortarefas.api.model.response.StatusResponse;
import br.com.rodrigo.gestortarefas.api.model.response.VendaResponse;
import br.com.rodrigo.gestortarefas.api.repository.HistoricoStatusVendaRepository;
import br.com.rodrigo.gestortarefas.api.repository.VendaRepository;
import br.com.rodrigo.gestortarefas.api.services.HorarioFuncionamentoService;
import br.com.rodrigo.gestortarefas.api.services.ICliente;
import br.com.rodrigo.gestortarefas.api.services.IItemPedido;
import br.com.rodrigo.gestortarefas.api.services.IPagamento;
import br.com.rodrigo.gestortarefas.api.services.IProduto;
import br.com.rodrigo.gestortarefas.api.services.IStatus;
import br.com.rodrigo.gestortarefas.api.services.IVenda;
import br.com.rodrigo.gestortarefas.api.services.SseService;
import br.com.rodrigo.gestortarefas.api.util.MensagemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaServiceImpl implements IVenda, IItemPedido {

    private final VendaRepository vendaRepository;
    private final HistoricoStatusVendaRepository historicoStatusVendaRepository;
    private final ICliente clienteService;
    private final IPagamento pagamentoService;
    private final IStatus statusService;
    private final IProduto produtoService;
    private final SseService sseService;
    private final HorarioFuncionamentoService horarioFuncionamentoService;

    @Override
    @Transactional
    public VendaResponse criar(VendaForm vendaForm, Long id) {
//        if (!horarioFuncionamentoService.estaAbertoAgora()) {
//            throw new ViolacaoIntegridadeDadosException(
//                    MensagensError.HORARIO_FUNCIONAMENTO_FECHADO.getMessage());
//        }
        Venda venda = criaVenda(vendaForm, id);
        venda = vendaRepository.save(venda);
        salvarHistoricoStatus(venda, venda.getStatus(), venda.getStatus().getDescricao());
        String mensagemVendaCriada = MensagemUtil.criarMensagemVendaRealizada(venda);
        sseService.notificarPorPerfil(mensagemVendaCriada, Perfil.ADMINSTRADOR);

        return VendaMapper.entidadeParaResponse(venda);
    }

    @Override
    public Optional<VendaResponse> consultarPorId(Long id) {
        Optional<Venda> venda = vendaRepository.findById(id);
        return venda.map(VendaMapper::entidadeParaResponse);
    }

    @Override
    public Page<VendaResponse> listarPedidosCliente(int page, int size, String sort) {
        ClienteResponse cliente = clienteService.getClienteLogado()
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_AUTORIZADO.getMessage()));

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 ?
                Sort.Direction.fromString(sortParams[1]) : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<Venda> vendas = vendaRepository.findByClienteIdOrderByDataVendaDesc(cliente.getId(), pageable);

        return vendas.map(VendaMapper::entidadeParaResponse);
    }

    @Override
    public Page<VendaResponse> listarTodos(int page, int size, String sort,
                                           Long id, String nomeCliente, Long status,
                                           Long formaPagamento, BigDecimal valorMinimo,
                                           BigDecimal valorMaximo, LocalDateTime dataInicio,
                                           LocalDateTime dataFim) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Venda> vendas = vendaRepository.findAll(id, nomeCliente, status,
                formaPagamento, valorMinimo, valorMaximo, dataInicio, dataFim, pageable);

        return vendas.map(VendaMapper::entidadeParaResponse);
    }


    private Venda criaVenda(VendaForm vendaForm, Long id) {
        Venda venda = id == null ? new Venda() : vendaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.VENDA_NAO_ENCONTRADA.getMessage(id)));

        ClienteResponse clienteResponse = clienteService.consultarPorId(vendaForm.getCliente())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.CLIENTE_NAO_ENCONTRADO_POR_ID.getMessage(vendaForm.getCliente())));

        PagamentoResponse pagamentoResponse = pagamentoService.consultarPorId(vendaForm.getPagamento())
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.PAGAMENTO_NAO_ENCONTRADO_POR_ID.getMessage(vendaForm.getPagamento())));

        StatusResponse statusResponse = statusService.consultarPorId(Status.PENDENTE)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.STATUS_NAO_ENCONTRADO_POR_ID.getMessage(Status.PENDENTE)));

        List<Long> produtoIds = vendaForm.getItens().stream()
                .map(item -> item.getProduto().getId())
                .collect(Collectors.toList());

        List<Produto> produtos = produtoService.buscarPorIds(produtoIds);

        if (produtos.isEmpty()) {
            throw new ObjetoNaoEncontradoException(
                    MensagensError.PRODUTO_NAO_ENCONTRADO.getMessage(produtoIds));
        }

        venda.setCliente(ClienteMapper.responseParaEntidade(clienteResponse));
        venda.setPagamento(PagamentoMapper.responseParaEntidade(pagamentoResponse));
        venda.setStatus(StatusMapper.responseParaEntidade(statusResponse));
        venda.setTipoVenda(TipoVenda.valueOf(vendaForm.getTipoVenda()));
        venda.setDataVenda(LocalDateTime.now());

        List<ItemVenda> itens = ItemVendaMapper.formParaEntidade(vendaForm.getItens(), venda, produtos);
        processarItensPedido(itens, venda);
        venda.setItens(itens);

        return venda;
    }

    @Override
    public void processarItensPedido(List<ItemVenda> itens, Venda venda) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            Produto produto = produtoService.buscarPorId(item.getProduto().getId());
            item.setProduto(produto);
            item.setVenda(venda);
            item.setPreco(produto.getPreco());
            item.calcularValorTotal();
            valorTotal = valorTotal.add(item.getValorTotal());
        }

        BigDecimal porcentagemPagamento = venda.getPagamento().getPorcentagemAcrescimo();
        BigDecimal fatorPorcentagem = BigDecimal.ONE.add(
                porcentagemPagamento.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP ));
        valorTotal = valorTotal.multiply(fatorPorcentagem);
        venda.setValorTotal(valorTotal);
    }

    @Override
    @Transactional
    public VendaResponse atualizarStatus(Long id, Long statusId) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.VENDA_NAO_ENCONTRADA.getMessage(id)));

        StatusResponse statusResponse = statusService.consultarPorId(statusId)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.STATUS_NAO_ENCONTRADO_POR_ID.getMessage(statusId)));
        Status novoStatus = StatusMapper.responseParaEntidade(statusResponse);

        venda.setStatus(novoStatus);
        venda = vendaRepository.save(venda);
        salvarHistoricoStatus(venda, novoStatus, novoStatus.getDescricao());

        String mensagem = MensagemUtil.criarMensagemMudancaStatusVenda(venda, novoStatus.getDescricao());
        sseService.notificarUsuario(venda.getCliente().getUsuario().getId(), mensagem);

        return VendaMapper.entidadeParaResponse(venda);
    }

    private void salvarHistoricoStatus(Venda venda, Status novoStatus, String observacao) {
        HistoricoStatusVenda historico = new HistoricoStatusVenda();
        historico.setVenda(venda);
        historico.setStatus(novoStatus);
        historico.setObservacao(observacao);
        historicoStatusVendaRepository.save(historico);
    }
}
