package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlteracaoSenhaDTO {

    @NotBlank(message = "Informe a senha atual.")
    private String senhaAtual;

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 8, message = "A nova senha deve ter ao menos 8 caracteres.")
    private String novaSenha;

    @NotBlank(message = "Confirme a nova senha.")
    private String confirmacao;
}
