package br.com.rodrigo.gestortarefas.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PessoaResponse {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String telefone;
}