package br.com.rodrigo.gestortarefas.api.services.impl;

import br.com.rodrigo.gestortarefas.api.conversor.EmpresaMapper;
import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.form.EmpresaForm;
import br.com.rodrigo.gestortarefas.api.model.response.EmpresaResponse;
import br.com.rodrigo.gestortarefas.api.repository.EmpresaRepository;
import br.com.rodrigo.gestortarefas.api.services.IEmpresa;
import br.com.rodrigo.gestortarefas.api.util.ValidadorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements IEmpresa {

    private final EmpresaRepository empresaRepository;
    private final ValidadorUtil validadorUtil;

    @Override
    public EmpresaResponse criar(EmpresaForm empresaForm, Long id) {
        Empresa empresa = criarEmpresa(empresaForm, id);
        empresa = empresaRepository.save(empresa);
        return construirDto(empresa);
    }

    @Override
    public void deletar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.EMPRESA_NAO_ENCONTRADA.getMessage(id)));
        validadorUtil.validarApagarEmpresa(empresa.getId());
        empresaRepository.delete(empresa);
    }

    @Override
    public Optional<EmpresaResponse> consultarPorId(Long id) {
        Optional<Empresa> empresa = Optional.ofNullable(empresaRepository.findById(id).orElseThrow(
                () -> new ObjetoNaoEncontradoException(MensagensError.EMPRESA_NAO_ENCONTRADA.getMessage(id))));
        return empresa.map(this::construirDto);
    }

    @Override
    public Page<EmpresaResponse> listarTodos(int page, int size, String sort, String nome, String cnpj, String telefone) {
        Sort sorting = Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sorting);

        return empresaRepository.findAll(nome, cnpj, telefone, pageable)
                .map(EmpresaMapper::entidadeParaResponse);
    }

    private Empresa criarEmpresa(EmpresaForm empresaForm, Long id) {
        Empresa empresa = id == null ? new Empresa() : empresaRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(
                        MensagensError.VENDA_NAO_ENCONTRADA.getMessage(id)));
        EmpresaMapper.atualizarEntidade(empresa, empresaForm);

        return empresa;
    }

    private EmpresaResponse construirDto(Empresa empresa) {
        return EmpresaMapper.entidadeParaResponse(empresa);
    }
}
