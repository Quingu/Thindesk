package com.pfc.thindesk.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespostaErroDTO {

    private int status;
    private String mensagem;
    private LocalDateTime dataHora;
}
