package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.TipoChamado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisicaoAtualizacaoChamadoDTO {

    @NotBlank(message = "Informe o título do chamado.")
    @Size(min = 5, max = 120, message = "O título deve ter entre 5 e 120 caracteres.")
    private String titulo;

    @NotBlank(message = "Descreva o problema.")
    @Size(min = 10, message = "A descrição deve ter ao menos 10 caracteres.")
    private String descricao;

    @NotNull(message = "Selecione a prioridade.")
    private PrioridadeChamado prioridade;

    @NotNull(message = "Selecione o tipo de chamado.")
    private TipoChamado tipo;

    private String clienteId;
    private String setor;
    private String tecnico;
}
