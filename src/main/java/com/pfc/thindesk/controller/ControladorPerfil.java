package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.AlteracaoSenhaDTO;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.servico.ServicoUsuario;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorPerfil {

    private final ServicoUsuario servicoUsuario;

    public ControladorPerfil(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping("/perfil")
    public String exibirPerfil(Model modelo, Principal principal) {
        Usuario usuario = servicoUsuario.buscarPorEmail(principal.getName());
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("alteracaoSenha", new AlteracaoSenhaDTO());
        modelo.addAttribute("tituloPagina", "Meu perfil");
        modelo.addAttribute("subtituloPagina", "Dados da conta e segurança");
        return "usuario/perfil";
    }

    @PostMapping("/perfil/senha")
    public String alterarSenha(@Valid @ModelAttribute("alteracaoSenha") AlteracaoSenhaDTO dados,
                               BindingResult resultado, Principal principal,
                               RedirectAttributes redirecionamento, Model modelo) {
        if (resultado.hasErrors()) {
            modelo.addAttribute("usuario", servicoUsuario.buscarPorEmail(principal.getName()));
            modelo.addAttribute("tituloPagina", "Meu perfil");
            modelo.addAttribute("subtituloPagina", "Dados da conta e segurança");
            return "usuario/perfil";
        }

        try {
            servicoUsuario.alterarSenha(principal.getName(), dados);
        } catch (RegraDeNegocioException excecao) {
            modelo.addAttribute("usuario", servicoUsuario.buscarPorEmail(principal.getName()));
            modelo.addAttribute("erro", excecao.getMessage());
            modelo.addAttribute("tituloPagina", "Meu perfil");
            modelo.addAttribute("subtituloPagina", "Dados da conta e segurança");
            return "usuario/perfil";
        }

        redirecionamento.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        return "redirect:/perfil";
    }
}
