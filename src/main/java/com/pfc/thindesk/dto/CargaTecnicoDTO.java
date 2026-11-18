package com.pfc.thindesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargaTecnicoDTO {

    private String tecnico;
    private String nome;
    private long total;
    private long resolvidos;
    private long percentual;
}
