package br.com.rodrigo.gestortarefas.api.model.form;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String senha;
}
