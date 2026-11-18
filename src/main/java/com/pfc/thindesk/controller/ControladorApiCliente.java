package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.ClienteDTO;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.servico.ServicoCliente;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ControladorApiCliente {

    private final ServicoCliente servicoCliente;

    public ControladorApiCliente(ServicoCliente servicoCliente) {
        this.servicoCliente = servicoCliente;
    }

    @GetMapping
    public List<Cliente> listar() {
        return servicoCliente.listarClientes();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable String id) {
        return servicoCliente.buscarPorId(id);
    }

    @PostMapping
    public Cliente criar(@Valid @RequestBody ClienteDTO dados) {
        return servicoCliente.salvar(dados);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable String id, @Valid @RequestBody ClienteDTO dados) {
        return servicoCliente.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable String id) {
        servicoCliente.excluir(id);
    }
}
