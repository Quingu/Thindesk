package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.Chamado;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PainelDTO {

    private long total;
    private long abertos;
    private long emAndamento;
    private long aguardando;
    private long resolvidos;
    private long cancelados;
    private long criticos;
    private long abertosHoje;
    private long totalClientes;
    private long totalUsuarios;
    private double taxaResolucao;
    private double tempoMedioHoras;

    private List<String> rotulosSituacao = new ArrayList<>();
    private List<Long> valoresSituacao = new ArrayList<>();
    private List<String> rotulosPrioridade = new ArrayList<>();
    private List<Long> valoresPrioridade = new ArrayList<>();
    private List<String> rotulosDias = new ArrayList<>();
    private List<Long> valoresDias = new ArrayList<>();
    private List<Chamado> recentes = new ArrayList<>();
    private List<CargaTecnicoDTO> cargaTecnicos = new ArrayList<>();
}
