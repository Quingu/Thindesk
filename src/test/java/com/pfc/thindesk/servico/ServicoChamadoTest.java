package com.pfc.thindesk.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.dto.RequisicaoChamadoDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.excecao.RecursoNaoEncontradoException;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.repositorio.RepositorioChamado;
import com.pfc.thindesk.repositorio.RepositorioCliente;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoChamadoTest {

    @Mock
    private RepositorioChamado repositorioChamado;

    @Mock
    private RepositorioCliente repositorioCliente;

    @InjectMocks
    private ServicoChamado servicoChamado;

    @Test
    void deveAbrirChamadoComSituacaoAbertaEProtocoloGerado() {
        when(repositorioChamado.count()).thenReturn(0L);
        when(repositorioChamado.existsByProtocolo(anyString())).thenReturn(false);
        when(repositorioChamado.save(any(Chamado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        RequisicaoChamadoDTO dados = new RequisicaoChamadoDTO(
                "Impressora sem conexão",
                "A impressora do setor financeiro parou de responder após a troca do switch.",
                PrioridadeChamado.ALTA, TipoChamado.IMPRESSORA, null, "Financeiro", null);

        Chamado chamado = servicoChamado.abrir(dados, "carla@thindesk.com", "Carla Souza");

        assertEquals(SituacaoChamado.ABERTO, chamado.getSituacao());
        assertEquals("Carla Souza", chamado.getSolicitanteNome());
        assertNotNull(chamado.getDataAbertura());
        assertTrue(chamado.getProtocolo().startsWith("TD-"));
    }

    @Test
    void deveResolverChamadoRegistrandoSolucaoEData() {
        Chamado chamado = chamadoAberto();

        when(repositorioChamado.findById("1")).thenReturn(Optional.of(chamado));
        when(repositorioChamado.save(any(Chamado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Chamado resolvido = servicoChamado.resolver("1",
                "Cabo de rede substituído e ponto testado com certificador.", "tecnico@thindesk.com");

        assertEquals(SituacaoChamado.RESOLVIDO, resolvido.getSituacao());
        assertNotNull(resolvido.getDataResolucao());
        assertEquals("tecnico@thindesk.com", resolvido.getTecnico());
    }

    @Test
    void naoDeveResolverChamadoJaResolvido() {
        Chamado chamado = chamadoAberto();
        chamado.setSituacao(SituacaoChamado.RESOLVIDO);

        when(repositorioChamado.findById("1")).thenReturn(Optional.of(chamado));

        assertThrows(RegraDeNegocioException.class,
                () -> servicoChamado.resolver("1", "Solução aplicada novamente.", "tecnico@thindesk.com"));
    }

    @Test
    void deveLancarExcecaoAoBuscarChamadoInexistente() {
        when(repositorioChamado.findById("999")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> servicoChamado.buscarPorId("999"));
    }

    private Chamado chamadoAberto() {
        Chamado chamado = new Chamado();
        chamado.setId("1");
        chamado.setProtocolo("TD-2026-0001");
        chamado.setTitulo("VPN instável");
        chamado.setSituacao(SituacaoChamado.ABERTO);
        chamado.setPrioridade(PrioridadeChamado.ALTA);
        chamado.setTipo(TipoChamado.REDE);
        chamado.setSolicitanteEmail("elisa@thindesk.com");
        chamado.setDataAbertura(java.time.LocalDateTime.now().minusHours(3));
        return chamado;
    }
}
