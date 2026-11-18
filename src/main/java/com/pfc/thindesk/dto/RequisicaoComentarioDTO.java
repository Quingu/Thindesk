package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoComentarioDTO {

    @NotBlank(message = "Escreva um comentário.")
    @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres.")
    private String mensagem;
}
