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
public class ClienteResponse {
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private String cpf;
    private LocalDate dataNascimento;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
