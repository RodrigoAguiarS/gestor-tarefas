package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioForm {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "O CPF informado é inválido.")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória.")
    private LocalDate dataNascimento;

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
    private String telefone;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email informado é inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    @NotBlank(message = "O cargo é obrigatório.")
    @Size(max = 50, message = "O cargo deve ter no máximo 50 caracteres.")
    private String cargo;

    @NotNull(message = "O salário é obrigatório.")
    private Double salario;

    @NotNull(message = "O perfil é obrigatório.")
    private Long perfil;
}
