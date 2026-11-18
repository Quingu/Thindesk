package com.pfc.thindesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    @NotBlank(message = "Informe o nome do cliente.")
    private String nome;

    @NotBlank(message = "Informe o telefone de contato.")
    private String telefone;

    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Informe o setor.")
    private String setor;

    private boolean ativo = true;
}
