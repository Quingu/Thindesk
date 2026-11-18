package com.pfc.thindesk.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    private String autor;
    private String perfil;
    private String mensagem;
    private LocalDateTime dataCriacao;
}
