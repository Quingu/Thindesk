package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.RequisicaoAtualizacaoChamadoDTO;
import com.pfc.thindesk.dto.RequisicaoChamadoDTO;
import com.pfc.thindesk.dto.RequisicaoComentarioDTO;
import com.pfc.thindesk.dto.RequisicaoResolucaoChamadoDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.servico.ServicoChamado;
import com.pfc.thindesk.servico.ServicoCliente;
import com.pfc.thindesk.servico.ServicoUsuario;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chamados")
public class ControladorChamado {

    private final ServicoChamado servicoChamado;
    private final ServicoCliente servicoCliente;
    private final ServicoUsuario servicoUsuario;

    public ControladorChamado(ServicoChamado servicoChamado, ServicoCliente servicoCliente,
                              ServicoUsuario servicoUsuario) {
        this.servicoChamado = servicoChamado;
        this.servicoCliente = servicoCliente;
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false) SituacaoChamado situacao,
                         @RequestParam(required = false) PrioridadeChamado prioridade,
                         @RequestParam(defaultValue = "false") boolean meus,
                         Authentication autenticacao, Model modelo) {
        boolean gestor = possuiPerfilGestao(autenticacao);
        boolean somenteMeus = meus || !gestor;

        List<Chamado> chamados = servicoChamado.listar(busca, situacao, prioridade,
                somenteMeus ? autenticacao.getName() : null);

        modelo.addAttribute("chamados", chamados);
        modelo.addAttribute("busca", busca);
        modelo.addAttribute("situacaoSelecionada", situacao);
        modelo.addAttribute("prioridadeSelecionada", prioridade);
        modelo.addAttribute("meus", meus);
        modelo.addAttribute("gestor", gestor);
        modelo.addAttribute("situacoes", SituacaoChamado.values());
        modelo.addAttribute("prioridades", PrioridadeChamado.values());
        modelo.addAttribute("tituloPagina", "Chamados");
        modelo.addAttribute("subtituloPagina", "Solicitações de suporte técnico");
        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovo(Model modelo) {
        prepararFormulario(modelo, new RequisicaoChamadoDTO(), false, null);
        return "chamados/formulario";
    }

    @PostMapping("/novo")
    public String abrirChamado(@Valid @ModelAttribute("chamadoForm") RequisicaoChamadoDTO dados,
                               BindingResult resultado, Principal principal, Model modelo,
                               RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            prepararFormulario(modelo, dados, false, null);
            return "chamados/formulario";
        }

        Usuario solicitante = servicoUsuario.buscarPorEmail(principal.getName());
        Chamado chamado = servicoChamado.abrir(dados, solicitante.getEmail(), solicitante.getNome());
        redirecionamento.addFlashAttribute("sucesso",
                "Chamado " + chamado.getProtocolo() + " aberto com sucesso.");
        return "redirect:/chamados/" + chamado.getId();
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable String id, Authentication autenticacao, Model modelo) {
        Chamado chamado = servicoChamado.buscarPorId(id);
        verificarAcesso(chamado, autenticacao);

        modelo.addAttribute("chamado", chamado);
        modelo.addAttribute("situacoes", SituacaoChamado.values());
        modelo.addAttribute("tecnicos", listarTecnicos());
        modelo.addAttribute("resolucao", new RequisicaoResolucaoChamadoDTO());
        modelo.addAttribute("comentario", new RequisicaoComentarioDTO());
        modelo.addAttribute("gestor", possuiPerfilGestao(autenticacao));
        modelo.addAttribute("tituloPagina", chamado.getProtocolo());
        modelo.addAttribute("subtituloPagina", chamado.getTitulo());
        return "chamados/detalhe";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable String id, Authentication autenticacao, Model modelo) {
        Chamado chamado = servicoChamado.buscarPorId(id);
        verificarAcesso(chamado, autenticacao);
        verificarEdicaoPermitida(chamado, autenticacao);

        RequisicaoAtualizacaoChamadoDTO dados = new RequisicaoAtualizacaoChamadoDTO();
        dados.setTitulo(chamado.getTitulo());
        dados.setDescricao(chamado.getDescricao());
        dados.setPrioridade(chamado.getPrioridade());
        dados.setTipo(chamado.getTipo());
        dados.setClienteId(chamado.getClienteId());
        dados.setSetor(chamado.getSetor());
        dados.setTecnico(chamado.getTecnico());

        prepararFormulario(modelo, dados, true, chamado.getId());
        return "chamados/formulario";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("chamadoForm") RequisicaoAtualizacaoChamadoDTO dados,
                            BindingResult resultado, Authentication autenticacao, Model modelo,
                            RedirectAttributes redirecionamento) {
        Chamado chamado = servicoChamado.buscarPorId(id);
        verificarAcesso(chamado, autenticacao);
        verificarEdicaoPermitida(chamado, autenticacao);

        if (resultado.hasErrors()) {
            prepararFormulario(modelo, dados, true, id);
            return "chamados/formulario";
        }

        try {
            servicoChamado.atualizar(id, dados);
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
            return "redirect:/chamados/" + id;
        }

        redirecionamento.addFlashAttribute("sucesso", "Chamado atualizado com sucesso.");
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/situacao")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String alterarSituacao(@PathVariable String id, @RequestParam SituacaoChamado situacao,
                                  RedirectAttributes redirecionamento) {
        try {
            servicoChamado.alterarSituacao(id, situacao);
            redirecionamento.addFlashAttribute("sucesso", "Situação atualizada.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/tecnico")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String atribuirTecnico(@PathVariable String id, @RequestParam(required = false) String tecnico,
                                  RedirectAttributes redirecionamento) {
        try {
            servicoChamado.atribuirTecnico(id, tecnico);
            redirecionamento.addFlashAttribute("sucesso", "Técnico atualizado.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('GERENTE','ADMINISTRADOR')")
    public String resolver(@PathVariable String id,
                           @Valid @ModelAttribute("resolucao") RequisicaoResolucaoChamadoDTO dados,
                           BindingResult resultado, Principal principal, RedirectAttributes redirecionamento) {
        if (resultado.hasErrors()) {
            redirecionamento.addFlashAttribute("erro", "Descreva a solução com ao menos 10 caracteres.");
            return "redirect:/chamados/" + id;
        }

        try {
            servicoChamado.resolver(id, dados.getSolucao(), principal.getName());
            redirecionamento.addFlashAttribute("sucesso", "Chamado resolvido com sucesso.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/comentario")
    public String comentar(@PathVariable String id,
                           @Valid @ModelAttribute("comentario") RequisicaoComentarioDTO dados,
                           BindingResult resultado, Authentication autenticacao,
                           RedirectAttributes redirecionamento) {
        Chamado chamado = servicoChamado.buscarPorId(id);
        verificarAcesso(chamado, autenticacao);

        if (resultado.hasErrors()) {
            redirecionamento.addFlashAttribute("erro", "Escreva um comentário válido.");
            return "redirect:/chamados/" + id;
        }

        String perfil = autenticacao.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        servicoChamado.comentar(id, dados.getMensagem(), autenticacao.getName(), perfil);
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable String id, Authentication autenticacao,
                           RedirectAttributes redirecionamento) {
        Chamado chamado = servicoChamado.buscarPorId(id);
        verificarAcesso(chamado, autenticacao);

        try {
            servicoChamado.cancelar(id);
            redirecionamento.addFlashAttribute("sucesso", "Chamado cancelado.");
        } catch (RegraDeNegocioException excecao) {
            redirecionamento.addFlashAttribute("erro", excecao.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String excluir(@PathVariable String id, RedirectAttributes redirecionamento) {
        servicoChamado.excluir(id);
        redirecionamento.addFlashAttribute("sucesso", "Chamado excluído.");
        return "redirect:/chamados";
    }

    private void prepararFormulario(Model modelo, Object dados, boolean edicao, String idChamado) {
        modelo.addAttribute("chamadoForm", dados);
        modelo.addAttribute("modoEdicao", edicao);
        modelo.addAttribute("idChamado", idChamado);
        modelo.addAttribute("clientes", servicoCliente.listarAtivos());
        modelo.addAttribute("tecnicos", listarTecnicos());
        modelo.addAttribute("tipos", TipoChamado.values());
        modelo.addAttribute("prioridades", PrioridadeChamado.values());
        modelo.addAttribute("tituloPagina", edicao ? "Editar chamado" : "Novo chamado");
        modelo.addAttribute("subtituloPagina", "Preencha os dados da solicitação");
    }

    private List<Usuario> listarTecnicos() {
        return servicoUsuario.listarUsuarios().stream()
                .filter(usuario -> usuario.getPerfil() != Perfil.USUARIO && usuario.isAtivo())
                .toList();
    }

    private boolean possuiPerfilGestao(Authentication autenticacao) {
        return autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> autoridade.getAuthority().equals("ROLE_GERENTE")
                        || autoridade.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }

    private void verificarAcesso(Chamado chamado, Authentication autenticacao) {
        if (possuiPerfilGestao(autenticacao)) {
            return;
        }
        if (!chamado.getSolicitanteEmail().equalsIgnoreCase(autenticacao.getName())) {
            throw new AccessDeniedException("Você não tem acesso a este chamado.");
        }
    }

    private void verificarEdicaoPermitida(Chamado chamado, Authentication autenticacao) {
        if (possuiPerfilGestao(autenticacao)) {
            return;
        }
        if (chamado.getSituacao() != SituacaoChamado.ABERTO
                && chamado.getSituacao() != SituacaoChamado.AGUARDANDO_CLIENTE) {
            throw new AccessDeniedException("Este chamado não pode mais ser editado.");
        }
    }
}
