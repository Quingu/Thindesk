package com.pfc.thindesk.entity;

import lombok.Getter;

@Getter
public enum PrioridadeChamado {

    BAIXA("Baixa", "prio-baixa"),
    MEDIA("Média", "prio-media"),
    ALTA("Alta", "prio-alta"),
    CRITICA("Crítica", "prio-critica");

    private final String rotulo;
    private final String classe;

    PrioridadeChamado(String rotulo, String classe) {
        this.rotulo = rotulo;
        this.classe = classe;
    }
}
