package com.pfc.thindesk.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.excecao.RecursoNaoEncontradoException;
import com.pfc.thindesk.excecao.TratadorGlobalDeExcecoes;
import com.pfc.thindesk.servico.ServicoChamado;
import com.pfc.thindesk.servico.ServicoUsuario;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ControladorApiChamadoTest {

    @Mock
    private ServicoChamado servicoChamado;

    @Mock
    private ServicoUsuario servicoUsuario;

    @InjectMocks
    private ControladorApiChamado controladorApiChamado;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(controladorApiChamado)
                .setControllerAdvice(new TratadorGlobalDeExcecoes())
                .build();
    }

    @Test
    void deveListarChamadosEmJson() throws Exception {
        Chamado chamado = new Chamado();
        chamado.setId("1");
        chamado.setProtocolo("TD-2026-0001");
        chamado.setTitulo("Impressora sem conexão");
        chamado.setSituacao(SituacaoChamado.ABERTO);
        chamado.setPrioridade(PrioridadeChamado.ALTA);
        chamado.setTipo(TipoChamado.IMPRESSORA);
        chamado.setDataAbertura(LocalDateTime.now());

        when(servicoChamado.listar(null, null, null, null)).thenReturn(List.of(chamado));

        mockMvc.perform(get("/api/chamados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].protocolo").value("TD-2026-0001"))
                .andExpect(jsonPath("$[0].situacao").value("ABERTO"));
    }

    @Test
    void deveRetornar404QuandoChamadoNaoExistir() throws Exception {
        when(servicoChamado.buscarPorId("999"))
                .thenThrow(new RecursoNaoEncontradoException("Chamado não encontrado."));

        mockMvc.perform(get("/api/chamados/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Chamado não encontrado."));
    }
}
