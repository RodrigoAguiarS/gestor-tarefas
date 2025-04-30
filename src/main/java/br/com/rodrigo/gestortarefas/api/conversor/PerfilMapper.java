package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Perfil;
import br.com.rodrigo.gestortarefas.api.model.form.PerfilForm;
import br.com.rodrigo.gestortarefas.api.model.response.PerfilResponse;

public class PerfilMapper {

    public static PerfilResponse entidadeParaResponse(Perfil perfil) {
        if (perfil == null) {
            return null;
        }

        return PerfilResponse.builder()
                .id(perfil.getId())
                .nome(perfil.getNome())
                .descricao(perfil.getDescricao())
                .ativo(perfil.getAtivo())
                .build();
    }

    public static Perfil requestParaEntidade(PerfilForm perfilForm) {
        if (perfilForm == null) {
            return null;
        }

        Perfil perfil = new Perfil();
        perfil.setNome(perfilForm.getNome());
        perfil.setDescricao(perfilForm.getDescricao());
        return perfil;
    }

    public static void atualizarEntidade(Perfil perfil, PerfilForm perfilForm) {
        if (perfilForm == null || perfil == null) {
            return;
        }

        perfil.setNome(perfilForm.getNome());
        perfil.setDescricao(perfilForm.getDescricao());
    }
}
