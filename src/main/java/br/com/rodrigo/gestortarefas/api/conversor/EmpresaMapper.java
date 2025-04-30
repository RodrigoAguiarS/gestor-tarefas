package br.com.rodrigo.gestortarefas.api.conversor;

import br.com.rodrigo.gestortarefas.api.model.Empresa;
import br.com.rodrigo.gestortarefas.api.model.Endereco;
import br.com.rodrigo.gestortarefas.api.model.form.EmpresaForm;
import br.com.rodrigo.gestortarefas.api.model.response.EmpresaResponse;

public class EmpresaMapper {

    public static Empresa formParaEntidade(EmpresaForm form) {
        Endereco endereco = Endereco.builder()
                .rua(form.getRua())
                .bairro(form.getBairro())
                .cidade(form.getCidade())
                .estado(form.getEstado())
                .cep(form.getCep())
                .numero(form.getNumero())
                .build();

        return Empresa.builder()
                .nome(form.getNome())
                .cnpj(form.getCnpj())
                .endereco(endereco)
                .telefone(form.getTelefone())
                .build();
    }

    public static EmpresaResponse entidadeParaResponse(Empresa empresa) {
        return EmpresaResponse.builder()
                .id(empresa.getId())
                .nome(empresa.getNome())
                .cnpj(empresa.getCnpj())
                .endereco(empresa.getEndereco())
                .horariosFuncionamento(empresa.getHorariosFuncionamento() != null ?
                        HorarioFuncionamentoMapper.listaEntidadeParaResponse(empresa.getHorariosFuncionamento()) : null)
                .telefone(empresa.getTelefone())
                .build();
    }

    public static void atualizarEntidade(Empresa empresa, EmpresaForm form) {
        empresa.setNome(form.getNome());
        empresa.setCnpj(form.getCnpj());
        empresa.setTelefone(form.getTelefone());

        if (empresa.getEndereco() == null) {
            empresa.setEndereco(new Endereco());
        }

        empresa.getEndereco().setRua(form.getRua());
        empresa.getEndereco().setBairro(form.getBairro());
        empresa.getEndereco().setCidade(form.getCidade());
        empresa.getEndereco().setEstado(form.getEstado());
        empresa.getEndereco().setCep(form.getCep());
        empresa.getEndereco().setNumero(form.getNumero());
    }
}