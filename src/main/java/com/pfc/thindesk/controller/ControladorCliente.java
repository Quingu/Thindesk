package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.ClienteDTO;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.servico.ServicoCliente;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/clientes")
public class ControladorCliente {

    private final ServicoCliente servicoCliente;

    public ControladorCliente(ServicoCliente servicoCliente) {
        this.servicoCliente = servicoCliente;
    }

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("clientes", servicoCliente.listarClientes());
        modelo.addAttribute("tituloPagina", "Clientes");
        modelo.addAttribute("subtituloPagina", "Empresas e setores atendidos");
        return "clientes/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String exibirFormularioNovo(Model modelo) {
        prepararFormulario(modelo, new ClienteDTO(), false, null);
        return "clientes/formulario";
    }

    @PostMapping("/novo")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String salvar(@Valid @ModelAttribute("clienteForm") ClienteDTO dados,
                         BindingResult resultado, Model modelo, RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            prepararFormulario(modelo, dados, false, null);
            return "clientes/formulario";
        }

        try {
            servicoCliente.salvar(dados);
        } catch (RegraDeNegocioException excecao) {
            modelo.addAttribute("erro", excecao.getMessage());
            prepararFormulario(modelo, dados, false, null);
            return "clientes/formulario";
        }

        redirecionamento.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String exibirFormularioEdicao(@PathVariable String id, Model modelo) {
        Cliente cliente = servicoCliente.buscarPorId(id);
        ClienteDTO dados = new ClienteDTO(cliente.getNome(), cliente.getTelefone(),
                cliente.getEmail(), cliente.getSetor(), cliente.isAtivo());

        prepararFormulario(modelo, dados, true, cliente.getId());
        return "clientes/formulario";
    }

    @PostMapping("/{id}/editar")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("clienteForm") ClienteDTO dados,
                            BindingResult resultado, Model modelo, RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            prepararFormulario(modelo, dados, true, id);
            return "clientes/formulario";
        }

        try {
            servicoCliente.atualizar(id, dados);
        } catch (RegraDeNegocioException excecao) {
            modelo.addAttribute("erro", excecao.getMessage());
            prepararFormulario(modelo, dados, true, id);
            return "clientes/formulario";
        }

        redirecionamento.addFlashAttribute("sucesso", "Cliente atualizado com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String excluir(@PathVariable String id, RedirectAttributes redirecionamento) {
        try {
            servicoCliente.excluir(id);
            redirecionamento.addFlashAttribute("sucesso", "Cliente excluído.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/clientes";
    }

    private void prepararFormulario(Model modelo, ClienteDTO dados, boolean edicao, String id) {
        modelo.addAttribute("clienteForm", dados);
        modelo.addAttribute("modoEdicao", edicao);
        modelo.addAttribute("idCliente", id);
        modelo.addAttribute("tituloPagina", edicao ? "Editar cliente" : "Novo cliente");
        modelo.addAttribute("subtituloPagina", "Cadastro de clientes atendidos");
    }
}
