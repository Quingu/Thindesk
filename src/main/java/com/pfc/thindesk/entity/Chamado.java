package com.pfc.thindesk.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chamados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chamado {

    @Id
    private String id;

    @Indexed(unique = true)
    private String protocolo;

    private String titulo;
    private String descricao;
    private SituacaoChamado situacao;
    private PrioridadeChamado prioridade;
    private TipoChamado tipo;

    private String solicitanteEmail;
    private String solicitanteNome;
    private String tecnico;

    private String clienteId;
    private String clienteNome;
    private String setor;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataResolucao;

    private String solucao;
    private Integer avaliacao;

    private List<Comentario> comentarios = new ArrayList<>();
}
