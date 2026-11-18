package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoResolucaoChamadoDTO {

    @NotBlank(message = "Descreva a solução aplicada.")
    @Size(min = 10, message = "A solução deve ter ao menos 10 caracteres.")
    private String solucao;
}
