package com.pfc.thindesk.repositorio;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioChamado extends MongoRepository<Chamado, String> {

    List<Chamado> findAllByOrderByDataAberturaDesc();

    List<Chamado> findBySolicitanteEmailOrderByDataAberturaDesc(String email);

    List<Chamado> findBySituacaoOrderByDataAberturaDesc(SituacaoChamado situacao);

    List<Chamado> findByTecnicoOrderByDataAberturaDesc(String tecnico);

    List<Chamado> findTop5ByOrderByDataAberturaDesc();

    boolean existsByProtocolo(String protocolo);

    long countBySituacao(SituacaoChamado situacao);

    long countByPrioridade(PrioridadeChamado prioridade);

    long countByClienteId(String clienteId);

    long countByTecnico(String tecnico);

    long countByTecnicoAndSituacao(String tecnico, SituacaoChamado situacao);

    long countByDataAberturaAfter(LocalDateTime data);
}
