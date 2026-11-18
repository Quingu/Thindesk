package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.CadastroUsuarioDTO;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.servico.ServicoUsuario;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorAutenticacao {

    private final ServicoUsuario servicoUsuario;

    public ControladorAutenticacao(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping("/cadastro")
    public String exibirCadastro(Model modelo) {
        modelo.addAttribute("cadastro", new CadastroUsuarioDTO());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("cadastro") CadastroUsuarioDTO dados,
                            BindingResult resultado, Model modelo,
                            RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            return "cadastro";
        }

        try {
            servicoUsuario.cadastrarUsuario(dados);
        } catch (RegraDeNegocioException excecao) {
            modelo.addAttribute("erro", excecao.getMessage());
            return "cadastro";
        }

        redirecionamento.addFlashAttribute("sucesso", "Conta criada com sucesso.");
        return "redirect:/login?cadastrado";
    }
}
