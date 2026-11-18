package com.pfc.thindesk.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    private String id;
    private String nome;
    private String telefone;
    private String email;
    private String setor;
    private boolean ativo;
    private LocalDateTime dataCadastro;
}
