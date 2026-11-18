package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.HorarioAtendimentoDTO;
import com.pfc.thindesk.servico.ServicoHorarioAtendimento;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ajustes-horarios")
public class ControladorHorarioAtendimento {

    private final ServicoHorarioAtendimento servicoHorario;

    public ControladorHorarioAtendimento(ServicoHorarioAtendimento servicoHorario) {
        this.servicoHorario = servicoHorario;
    }

    @GetMapping
    public String listar(Model modelo) {
        if (!modelo.containsAttribute("horarioForm")) {
            modelo.addAttribute("horarioForm", new HorarioAtendimentoDTO());
        }
        modelo.addAttribute("horarios", servicoHorario.listarHorarios());
        modelo.addAttribute("tituloPagina", "Horários de atendimento");
        modelo.addAttribute("subtituloPagina", "Janelas de atendimento por setor");
        return "horarios/lista";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("horarioForm") HorarioAtendimentoDTO dados,
                         BindingResult resultado, RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            redirecionamento.addFlashAttribute("erro", "Preencha todos os campos do horário.");
            return "redirect:/ajustes-horarios";
        }

        servicoHorario.salvar(dados);
        redirecionamento.addFlashAttribute("sucesso", "Horário cadastrado.");
        return "redirect:/ajustes-horarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id, RedirectAttributes redirecionamento) {
        servicoHorario.excluir(id);
        redirecionamento.addFlashAttribute("sucesso", "Horário removido.");
        return "redirect:/ajustes-horarios";
    }
}
