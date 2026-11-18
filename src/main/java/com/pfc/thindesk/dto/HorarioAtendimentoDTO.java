package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtendimentoDTO {

    @NotBlank(message = "Informe o setor.")
    private String setor;

    @NotBlank(message = "Informe o dia da semana.")
    private String diaSemana;

    @NotBlank(message = "Informe o horário de início.")
    private String horarioInicio;

    @NotBlank(message = "Informe o horário de fim.")
    private String horarioFim;
}
