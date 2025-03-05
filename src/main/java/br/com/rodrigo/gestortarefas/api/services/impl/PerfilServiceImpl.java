package br.com.rodrigo.gestortarefas.api.services.impl;


import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import br.com.rodrigo.gestortarefas.api.exception.ViolacaoIntegridadeDadosException;
import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.form.PerfilForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;
import br.com.rodrigo.gestortarefas.api.repository.PerfilRepository;
import br.com.rodrigo.gestortarefas.api.repository.UsuarioRepository;
import br.com.rodrigo.gestortarefas.api.services.IPerfil;
import br.com.rodrigo.gestortarefas.api.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements IPerfil {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public PerfilResponse criar(PerfilForm perfilForm) {
        Perfil perfil = criarEntidade(perfilForm, null);
        perfil = perfilRepository.save(perfil);
        return construirDto(perfil);
    }

    @Override
    public PerfilResponse atualizar(Long id, PerfilForm perfilForm) {
        Perfil perfil = criarEntidade(perfilForm, id);
        perfil = perfilRepository.save(perfil);
        return construirDto(perfil);
    }

    @Override
    public void deletar(Long id) {
        validarExclusao(id);
        perfilRepository.deleteById(id);
    }

    @Override
    public Optional<PerfilResponse> consultarPorId(Long id) {
        return perfilRepository.findById(id).map(this::construirDto);
    }

    @Override
    public Page<PerfilResponse> consultarTodos(Pageable pageable) {
        Page<Perfil> perfis = perfilRepository.findAll(pageable);
        return perfis.map(this::construirDto);
    }

    private Perfil criarEntidade(PerfilForm perfilForm, Long id) {
        Perfil perfil = ModelMapperUtil.map(perfilForm, Perfil.class);
        if (isNotEmpty(id)) {
            perfil.setId(id);
        }
        return perfil;
    }

    private PerfilResponse construirDto(Perfil perfil) {
        return ModelMapperUtil.map(perfil, PerfilResponse.class);
    }

    private void validarExclusao(Long id) {
        boolean existeUsuario = usuarioRepository.existsByPerfisId(id);
        if (existeUsuario) {
            throw new ViolacaoIntegridadeDadosException(MensagensError.PERFIL_POSSUI_USUARIO.getMessage());
        }
    }
}
