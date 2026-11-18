package com.pfc.thindesk.controller;

import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.servico.ServicoUsuario;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ControladorAdministrador {

    private final ServicoUsuario servicoUsuario;

    public ControladorAdministrador(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping("/painel")
    public String painel(Model modelo) {
        modelo.addAttribute("usuarios", servicoUsuario.listarUsuarios());
        modelo.addAttribute("perfis", Perfil.values());
        modelo.addAttribute("tituloPagina", "Administração");
        modelo.addAttribute("subtituloPagina", "Usuários, perfis e permissões de acesso");
        return "administrador/painel";
    }

    @PostMapping("/promover/{id}")
    public String alterarPerfil(@PathVariable String id, @RequestParam Perfil perfil,
                                RedirectAttributes redirecionamento) {
        try {
            servicoUsuario.atualizarPerfil(id, perfil);
            redirecionamento.addFlashAttribute("sucesso", "Perfil atualizado.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/administrador/painel";
    }

    @PostMapping("/ativar/{id}")
    public String alternarAtivo(@PathVariable String id, Principal principal,
                                RedirectAttributes redirecionamento) {
        try {
            servicoUsuario.alternarAtivo(id, principal.getName());
            redirecionamento.addFlashAttribute("sucesso", "Situação do usuário atualizada.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/administrador/painel";
    }
}
