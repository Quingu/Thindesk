package com.pfc.thindesk.entity;

import lombok.Getter;

@Getter
public enum SituacaoChamado {

    ABERTO("Aberto", "selo-aberto"),
    EM_ANDAMENTO("Em andamento", "selo-andamento"),
    AGUARDANDO_CLIENTE("Aguardando cliente", "selo-aguardando"),
    RESOLVIDO("Resolvido", "selo-resolvido"),
    CANCELADO("Cancelado", "selo-cancelado");

    private final String rotulo;
    private final String classe;

    SituacaoChamado(String rotulo, String classe) {
        this.rotulo = rotulo;
        this.classe = classe;
    }
}
