package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteForm {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    @NotBlank
    private String nome;
    @NotBlank
    private String telefone;
    @CPF
    @NotBlank
    private String cpf;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    @NotBlank
    private String cep;
    private Long empresaId;

}
