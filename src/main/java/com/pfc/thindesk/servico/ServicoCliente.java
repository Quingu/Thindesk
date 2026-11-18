package com.pfc.thindesk.servico;

import com.pfc.thindesk.dto.ClienteDTO;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.excecao.RecursoNaoEncontradoException;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.repositorio.RepositorioChamado;
import com.pfc.thindesk.repositorio.RepositorioCliente;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicoCliente {

    private final RepositorioCliente repositorioCliente;
    private final RepositorioChamado repositorioChamado;

    public ServicoCliente(RepositorioCliente repositorioCliente, RepositorioChamado repositorioChamado) {
        this.repositorioCliente = repositorioCliente;
        this.repositorioChamado = repositorioChamado;
    }

    public List<Cliente> listarClientes() {
        return repositorioCliente.findAllByOrderByNomeAsc();
    }

    public List<Cliente> listarAtivos() {
        return repositorioCliente.findByAtivoTrueOrderByNomeAsc();
    }

    public Cliente buscarPorId(String id) {
        return repositorioCliente.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    public Cliente salvar(ClienteDTO dados) {
        if (repositorioCliente.existsByNomeIgnoreCase(dados.getNome().trim())) {
            throw new RegraDeNegocioException("Já existe um cliente com este nome.");
        }

        Cliente cliente = new Cliente();
        aplicarDados(cliente, dados);
        cliente.setDataCadastro(LocalDateTime.now());
        return repositorioCliente.save(cliente);
    }

    public Cliente atualizar(String id, ClienteDTO dados) {
        Cliente cliente = buscarPorId(id);
        aplicarDados(cliente, dados);
        return repositorioCliente.save(cliente);
    }

    public void excluir(String id) {
        Cliente cliente = buscarPorId(id);

        if (repositorioChamado.countByClienteId(cliente.getId()) > 0) {
            throw new RegraDeNegocioException("Este cliente possui chamados vinculados e não pode ser excluído.");
        }

        repositorioCliente.delete(cliente);
    }

    private void aplicarDados(Cliente cliente, ClienteDTO dados) {
        cliente.setNome(dados.getNome().trim());
        cliente.setTelefone(dados.getTelefone().trim());
        cliente.setEmail(dados.getEmail() == null ? null : dados.getEmail().trim().toLowerCase());
        cliente.setSetor(dados.getSetor().trim());
        cliente.setAtivo(dados.isAtivo());
    }
}
