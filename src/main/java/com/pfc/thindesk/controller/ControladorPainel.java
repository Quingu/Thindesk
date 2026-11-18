package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.PainelDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.servico.ServicoChamado;
import com.pfc.thindesk.servico.ServicoPainel;
import com.pfc.thindesk.servico.ServicoUsuario;
import java.security.Principal;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorPainel {

    private final ServicoPainel servicoPainel;
    private final ServicoChamado servicoChamado;
    private final ServicoUsuario servicoUsuario;

    public ControladorPainel(ServicoPainel servicoPainel, ServicoChamado servicoChamado,
                             ServicoUsuario servicoUsuario) {
        this.servicoPainel = servicoPainel;
        this.servicoChamado = servicoChamado;
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication autenticacao, Principal principal, Model modelo) {
        boolean gestor = possuiPerfilGestao(autenticacao);
        PainelDTO painel = servicoPainel.montar(gestor ? null : principal.getName());

        modelo.addAttribute("painel", painel);
        modelo.addAttribute("gestor", gestor);
        modelo.addAttribute("nomeUsuario", servicoUsuario.buscarPorEmail(principal.getName()).getNome());
        modelo.addAttribute("tituloPagina", "Dashboard");
        modelo.addAttribute("subtituloPagina", gestor
                ? "Visão geral da central de atendimento"
                : "Acompanhe suas solicitações de suporte");
        return "painel/dashboard";
    }

    @GetMapping("/gerente/painel")
    public String painelGerente(Model modelo) {
        PainelDTO painel = servicoPainel.montar(null);

        List<Chamado> emAtendimento = servicoChamado.listar(null, null, null, null).stream()
                .filter(chamado -> chamado.getSituacao() == SituacaoChamado.ABERTO
                        || chamado.getSituacao() == SituacaoChamado.EM_ANDAMENTO
                        || chamado.getSituacao() == SituacaoChamado.AGUARDANDO_CLIENTE)
                .limit(10)
                .toList();

        modelo.addAttribute("painel", painel);
        modelo.addAttribute("chamados", emAtendimento);
        modelo.addAttribute("tituloPagina", "Painel do gerente");
        modelo.addAttribute("subtituloPagina", "Fila de atendimento e produtividade da equipe");
        return "painel/gerente";
    }

    private boolean possuiPerfilGestao(Authentication autenticacao) {
        return autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> autoridade.getAuthority().equals("ROLE_GERENTE")
                        || autoridade.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
}
