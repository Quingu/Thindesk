package com.pfc.thindesk.entity;

import lombok.Getter;

@Getter
public enum TipoChamado {

    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    REDE("Rede"),
    ACESSO("Acesso e permissões"),
    IMPRESSORA("Impressora"),
    OUTROS("Outros");

    private final String rotulo;

    TipoChamado(String rotulo) {
        this.rotulo = rotulo;
    }
}
