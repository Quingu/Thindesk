package com.pfc.thindesk.servico;

import com.pfc.thindesk.dto.RequisicaoAtualizacaoChamadoDTO;
import com.pfc.thindesk.dto.RequisicaoChamadoDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Comentario;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.excecao.RecursoNaoEncontradoException;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.repositorio.RepositorioChamado;
import com.pfc.thindesk.repositorio.RepositorioCliente;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ServicoChamado {

    private final RepositorioChamado repositorioChamado;
    private final RepositorioCliente repositorioCliente;

    public ServicoChamado(RepositorioChamado repositorioChamado, RepositorioCliente repositorioCliente) {
        this.repositorioChamado = repositorioChamado;
        this.repositorioCliente = repositorioCliente;
    }

    public Chamado abrir(RequisicaoChamadoDTO dados, String emailSolicitante, String nomeSolicitante) {
        Chamado chamado = new Chamado();
        chamado.setProtocolo(gerarProtocolo());
        chamado.setTitulo(dados.getTitulo().trim());
        chamado.setDescricao(dados.getDescricao().trim());
        chamado.setSituacao(SituacaoChamado.ABERTO);
        chamado.setPrioridade(dados.getPrioridade());
        chamado.setTipo(dados.getTipo());
        chamado.setSolicitanteEmail(emailSolicitante);
        chamado.setSolicitanteNome(nomeSolicitante);
        chamado.setTecnico(dados.getTecnico());
        chamado.setSetor(dados.getSetor());
        vincularCliente(chamado, dados.getClienteId());
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public List<Chamado> listar(String busca, SituacaoChamado situacao,
                                PrioridadeChamado prioridade, String emailSolicitante) {
        List<Chamado> chamados = emailSolicitante == null
                ? repositorioChamado.findAllByOrderByDataAberturaDesc()
                : repositorioChamado.findBySolicitanteEmailOrderByDataAberturaDesc(emailSolicitante);

        return chamados.stream()
                .filter(chamado -> situacao == null || chamado.getSituacao() == situacao)
                .filter(chamado -> prioridade == null || chamado.getPrioridade() == prioridade)
                .filter(chamado -> correspondeBusca(chamado, busca))
                .toList();
    }

    public Chamado buscarPorId(String id) {
        return repositorioChamado.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado."));
    }

    public Chamado atualizar(String id, RequisicaoAtualizacaoChamadoDTO dados) {
        Chamado chamado = buscarPorId(id);
        chamado.setTitulo(dados.getTitulo().trim());
        chamado.setDescricao(dados.getDescricao().trim());
        chamado.setPrioridade(dados.getPrioridade());
        chamado.setTipo(dados.getTipo());
        chamado.setSetor(dados.getSetor());
        chamado.setTecnico(dados.getTecnico());
        vincularCliente(chamado, dados.getClienteId());
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public Chamado alterarSituacao(String id, SituacaoChamado novaSituacao) {
        Chamado chamado = buscarPorId(id);

        if (chamado.getSituacao() == novaSituacao) {
            return chamado;
        }
        if (chamado.getSituacao() == SituacaoChamado.CANCELADO) {
            throw new RegraDeNegocioException("Chamados cancelados não podem ser reabertos.");
        }
        if (novaSituacao == SituacaoChamado.RESOLVIDO) {
            throw new RegraDeNegocioException("Utilize a ação de resolução para encerrar o chamado.");
        }

        chamado.setSituacao(novaSituacao);
        chamado.setDataResolucao(null);
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public Chamado atribuirTecnico(String id, String emailTecnico) {
        Chamado chamado = buscarPorId(id);

        if (chamado.getSituacao() == SituacaoChamado.RESOLVIDO
                || chamado.getSituacao() == SituacaoChamado.CANCELADO) {
            throw new RegraDeNegocioException("Chamados encerrados não podem receber novos técnicos.");
        }

        chamado.setTecnico(emailTecnico);
        if (chamado.getSituacao() == SituacaoChamado.ABERTO && emailTecnico != null && !emailTecnico.isBlank()) {
            chamado.setSituacao(SituacaoChamado.EM_ANDAMENTO);
        }
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public Chamado resolver(String id, String solucao, String emailTecnico) {
        Chamado chamado = buscarPorId(id);

        if (chamado.getSituacao() == SituacaoChamado.RESOLVIDO) {
            throw new RegraDeNegocioException("Este chamado já está resolvido.");
        }
        if (chamado.getSituacao() == SituacaoChamado.CANCELADO) {
            throw new RegraDeNegocioException("Chamados cancelados não podem ser resolvidos.");
        }
        if (solucao == null || solucao.trim().length() < 10) {
            throw new RegraDeNegocioException("Descreva a solução com ao menos 10 caracteres.");
        }

        if (chamado.getTecnico() == null || chamado.getTecnico().isBlank()) {
            chamado.setTecnico(emailTecnico);
        }
        chamado.setSolucao(solucao.trim());
        chamado.setSituacao(SituacaoChamado.RESOLVIDO);
        chamado.setDataResolucao(LocalDateTime.now());
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public Chamado comentar(String id, String mensagem, String autor, String perfil) {
        Chamado chamado = buscarPorId(id);

        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setPerfil(perfil);
        comentario.setMensagem(mensagem.trim());
        comentario.setDataCriacao(LocalDateTime.now());

        chamado.getComentarios().add(comentario);
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public Chamado cancelar(String id) {
        Chamado chamado = buscarPorId(id);

        if (chamado.getSituacao() == SituacaoChamado.RESOLVIDO) {
            throw new RegraDeNegocioException("Chamados resolvidos não podem ser cancelados.");
        }

        chamado.setSituacao(SituacaoChamado.CANCELADO);
        chamado.setDataAtualizacao(LocalDateTime.now());
        return repositorioChamado.save(chamado);
    }

    public void excluir(String id) {
        Chamado chamado = buscarPorId(id);
        repositorioChamado.delete(chamado);
    }

    public long contarTotal() {
        return repositorioChamado.count();
    }

    public String gerarProtocolo() {
        long sequencia = repositorioChamado.count() + 1;
        String protocolo;

        do {
            protocolo = String.format("TD-%d-%04d", Year.now().getValue(), sequencia++);
        } while (repositorioChamado.existsByProtocolo(protocolo));

        return protocolo;
    }

    private void vincularCliente(Chamado chamado, String clienteId) {
        if (clienteId == null || clienteId.isBlank()) {
            chamado.setClienteId(null);
            chamado.setClienteNome(null);
            return;
        }

        repositorioCliente.findById(clienteId).ifPresent(cliente -> {
            chamado.setClienteId(cliente.getId());
            chamado.setClienteNome(cliente.getNome());
            if (chamado.getSetor() == null || chamado.getSetor().isBlank()) {
                chamado.setSetor(cliente.getSetor());
            }
        });
    }

    private boolean correspondeBusca(Chamado chamado, String busca) {
        if (busca == null || busca.isBlank()) {
            return true;
        }

        String termo = busca.trim().toLowerCase(Locale.ROOT);
        return contem(chamado.getTitulo(), termo)
                || contem(chamado.getDescricao(), termo)
                || contem(chamado.getProtocolo(), termo)
                || contem(chamado.getClienteNome(), termo)
                || contem(chamado.getSolicitanteNome(), termo);
    }

    private boolean contem(String valor, String termo) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(termo);
    }
}
