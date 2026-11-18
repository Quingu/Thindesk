package com.pfc.thindesk.servico;

import com.pfc.thindesk.dto.CargaTecnicoDTO;
import com.pfc.thindesk.dto.PainelDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.repositorio.RepositorioChamado;
import com.pfc.thindesk.repositorio.RepositorioCliente;
import com.pfc.thindesk.repositorio.RepositorioUsuario;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ServicoPainel {

    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final Locale LOCAL_BRASIL = Locale.forLanguageTag("pt-BR");

    private final RepositorioChamado repositorioChamado;
    private final RepositorioCliente repositorioCliente;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoPainel(RepositorioChamado repositorioChamado, RepositorioCliente repositorioCliente,
                         RepositorioUsuario repositorioUsuario) {
        this.repositorioChamado = repositorioChamado;
        this.repositorioCliente = repositorioCliente;
        this.repositorioUsuario = repositorioUsuario;
    }

    public PainelDTO montar(String emailSolicitante) {
        List<Chamado> chamados = emailSolicitante == null
                ? repositorioChamado.findAllByOrderByDataAberturaDesc()
                : repositorioChamado.findBySolicitanteEmailOrderByDataAberturaDesc(emailSolicitante);

        PainelDTO painel = new PainelDTO();
        painel.setTotal(chamados.size());
        painel.setAbertos(contar(chamados, SituacaoChamado.ABERTO));
        painel.setEmAndamento(contar(chamados, SituacaoChamado.EM_ANDAMENTO));
        painel.setAguardando(contar(chamados, SituacaoChamado.AGUARDANDO_CLIENTE));
        painel.setResolvidos(contar(chamados, SituacaoChamado.RESOLVIDO));
        painel.setCancelados(contar(chamados, SituacaoChamado.CANCELADO));
        painel.setCriticos(chamados.stream()
                .filter(chamado -> chamado.getPrioridade() == PrioridadeChamado.CRITICA)
                .filter(chamado -> chamado.getSituacao() != SituacaoChamado.RESOLVIDO
                        && chamado.getSituacao() != SituacaoChamado.CANCELADO)
                .count());
        painel.setAbertosHoje(chamados.stream()
                .filter(chamado -> chamado.getDataAbertura() != null
                        && chamado.getDataAbertura().toLocalDate().equals(LocalDate.now()))
                .count());
        painel.setTotalClientes(repositorioCliente.count());
        painel.setTotalUsuarios(repositorioUsuario.count());

        long encerrados = painel.getResolvidos() + painel.getCancelados();
        painel.setTaxaResolucao(painel.getTotal() == 0 ? 0
                : Math.round((painel.getResolvidos() * 100.0 / painel.getTotal()) * 10.0) / 10.0);
        painel.setTempoMedioHoras(calcularTempoMedio(chamados));

        preencherSituacoes(painel, chamados);
        preencherPrioridades(painel, chamados);
        preencherUltimosDias(painel, chamados);
        preencherTecnicos(painel, chamados);

        painel.setRecentes(chamados.stream()
                .sorted(Comparator.comparing(Chamado::getDataAbertura).reversed())
                .limit(5)
                .toList());

        return painel;
    }

    private long contar(List<Chamado> chamados, SituacaoChamado situacao) {
        return chamados.stream().filter(chamado -> chamado.getSituacao() == situacao).count();
    }

    private double calcularTempoMedio(List<Chamado> chamados) {
        List<Long> minutos = chamados.stream()
                .filter(chamado -> chamado.getDataResolucao() != null && chamado.getDataAbertura() != null)
                .map(chamado -> Duration.between(chamado.getDataAbertura(), chamado.getDataResolucao()).toMinutes())
                .toList();

        if (minutos.isEmpty()) {
            return 0;
        }

        double media = minutos.stream().mapToLong(Long::longValue).average().orElse(0);
        return Math.round((media / 60.0) * 10.0) / 10.0;
    }

    private void preencherSituacoes(PainelDTO painel, List<Chamado> chamados) {
        for (SituacaoChamado situacao : SituacaoChamado.values()) {
            painel.getRotulosSituacao().add(situacao.getRotulo());
            painel.getValoresSituacao().add(contar(chamados, situacao));
        }
    }

    private void preencherPrioridades(PainelDTO painel, List<Chamado> chamados) {
        for (PrioridadeChamado prioridade : PrioridadeChamado.values()) {
            painel.getRotulosPrioridade().add(prioridade.getRotulo());
            painel.getValoresPrioridade().add(chamados.stream()
                    .filter(chamado -> chamado.getPrioridade() == prioridade)
                    .count());
        }
    }

    private void preencherUltimosDias(PainelDTO painel, List<Chamado> chamados) {
        for (int indice = 6; indice >= 0; indice--) {
            LocalDate dia = LocalDate.now().minusDays(indice);
            painel.getRotulosDias().add(dia.format(FORMATO_DIA));
            painel.getValoresDias().add(chamados.stream()
                    .filter(chamado -> chamado.getDataAbertura() != null
                            && chamado.getDataAbertura().toLocalDate().equals(dia))
                    .count());
        }
    }

    private void preencherTecnicos(PainelDTO painel, List<Chamado> chamados) {
        Map<String, Usuario> usuariosPorEmail = new LinkedHashMap<>();
        repositorioUsuario.findAll().forEach(usuario -> usuariosPorEmail.put(usuario.getEmail(), usuario));

        Map<String, CargaTecnicoDTO> carga = new LinkedHashMap<>();
        for (Chamado chamado : chamados) {
            if (chamado.getTecnico() == null || chamado.getTecnico().isBlank()) {
                continue;
            }

            CargaTecnicoDTO item = carga.computeIfAbsent(chamado.getTecnico(), email -> {
                Usuario usuario = usuariosPorEmail.get(email);
                return new CargaTecnicoDTO(email,
                        usuario == null ? email : usuario.getNome(), 0, 0, 0);
            });

            item.setTotal(item.getTotal() + 1);
            if (chamado.getSituacao() == SituacaoChamado.RESOLVIDO) {
                item.setResolvidos(item.getResolvidos() + 1);
            }
        }

        painel.setCargaTecnicos(carga.values().stream()
                .sorted(Comparator.comparingLong(CargaTecnicoDTO::getTotal).reversed())
                .limit(5)
                .toList());

        long maiorCarga = painel.getCargaTecnicos().stream()
                .mapToLong(CargaTecnicoDTO::getTotal)
                .max()
                .orElse(0);

        if (maiorCarga > 0) {
            painel.getCargaTecnicos().forEach(item ->
                    item.setPercentual(Math.max(8, Math.round(item.getTotal() * 100.0 / maiorCarga))));
        }
    }
}
