package br.com.rodrigo.gestortarefas.api.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaForm {

    @NotBlank(message = "O nome da empresa é obrigatório.")
    @Size(max = 100, message = "O nome da empresa deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "O CNPJ da empresa é obrigatório.")
    @Size(min = 14, max = 14, message = "O CNPJ deve ter 14 caracteres.")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter apenas números.")
    private String cnpj;

    @NotBlank(message = "A rua é obrigatória.")
    private String rua;

    @NotBlank(message = "O bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(max = 2, message = "O estado deve ter no máximo 2 caracteres.")
    private String estado;

    @NotBlank(message = "O CEP é obrigatório.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000.")
    private String cep;

    @NotBlank(message = "O número é obrigatório.")
    private String numero;

    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", message = "O telefone deve estar no formato (XX) XXXXX-XXXX.")
    private String telefone;
}