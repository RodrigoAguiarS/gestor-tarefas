package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ObjetoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Consumer;


public abstract class GenericServiceImpl<Entity, Form, Response> implements GenericService<Form, Response> {

    protected final JpaRepository<Entity, Long> repository;

    protected GenericServiceImpl(JpaRepository<Entity, Long> repository) {
        this.repository = repository;
    }

    protected abstract Entity criarEntidade(Form form, Long id);

    protected abstract Response construirDto(Entity entidade);

    protected abstract void ativar(Entity entidade);

    protected abstract void desativar(Entity entidade);

    protected String getEntidadeNome() {
        return "Entidade";
    }

    @Override
    public Response cadastrar(Form form) {
        Entity entidade = criarEntidade(form, null);
        entidade = repository.save(entidade);
        return construirDto(entidade);
    }

    @Override
    public Response atualizar(Long id, Form form) {
        Entity entidadeExistente = repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));

        mapNonNullFields(form, entidadeExistente);
        entidadeExistente = repository.save(entidadeExistente);
        return construirDto(entidadeExistente);
    }

    private void mapNonNullFields(Form form, Entity entidadeExistente) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(form, entidadeExistente);
    }

    @Override
    public Response obterPorId(Long id) {
        Optional<Entity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            return construirDto(optionalEntity.get());
        }
        throw new ObjetoNaoEncontradoException(MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome()));
    }

    @Override
    public Page<Response> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(this::construirDto);
    }

    @Override
    public void apagar(Long id) {
        repository.deleteById(id);
    }

    private void alterarStatus(Long id, Consumer<Entity> statusChanger) {
        Entity entidade = repository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException(MensagensError.ENTIDADE_NAO_ENCONTRADO.getMessage(getEntidadeNome())));
        statusChanger.accept(entidade);
        repository.save(entidade);
    }

    @Override
    public void ativar(Long id) {
        alterarStatus(id, this::ativar);
    }

    @Override
    public void desativar(Long id) {
        alterarStatus(id, this::desativar);
    }
}
