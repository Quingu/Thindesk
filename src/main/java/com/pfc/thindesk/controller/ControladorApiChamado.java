package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.RequisicaoAtualizacaoChamadoDTO;
import com.pfc.thindesk.dto.RequisicaoChamadoDTO;
import com.pfc.thindesk.dto.RequisicaoResolucaoChamadoDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.servico.ServicoChamado;
import com.pfc.thindesk.servico.ServicoUsuario;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chamados")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ControladorApiChamado {

    private final ServicoChamado servicoChamado;
    private final ServicoUsuario servicoUsuario;

    public ControladorApiChamado(ServicoChamado servicoChamado, ServicoUsuario servicoUsuario) {
        this.servicoChamado = servicoChamado;
        this.servicoUsuario = servicoUsuario;
    }

    @GetMapping
    public List<Chamado> listar(@RequestParam(required = false) String busca,
                                @RequestParam(required = false) SituacaoChamado situacao,
                                @RequestParam(required = false) PrioridadeChamado prioridade) {
        return servicoChamado.listar(busca, situacao, prioridade, null);
    }

    @GetMapping("/{id}")
    public Chamado buscar(@PathVariable String id) {
        return servicoChamado.buscarPorId(id);
    }

    @PostMapping
    public Chamado abrir(@Valid @RequestBody RequisicaoChamadoDTO dados, Principal principal) {
        Usuario solicitante = servicoUsuario.buscarPorEmail(principal.getName());
        return servicoChamado.abrir(dados, solicitante.getEmail(), solicitante.getNome());
    }

    @PutMapping("/{id}")
    public Chamado atualizar(@PathVariable String id, @Valid @RequestBody RequisicaoAtualizacaoChamadoDTO dados) {
        return servicoChamado.atualizar(id, dados);
    }

    @PutMapping("/{id}/resolver")
    public Chamado resolver(@PathVariable String id, @Valid @RequestBody RequisicaoResolucaoChamadoDTO dados,
                            Principal principal) {
        return servicoChamado.resolver(id, dados.getSolucao(), principal.getName());
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        servicoChamado.excluir(id);
    }
}
