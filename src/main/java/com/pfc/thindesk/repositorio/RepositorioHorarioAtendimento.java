package com.pfc.thindesk.repositorio;

import com.pfc.thindesk.entity.HorarioAtendimento;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioHorarioAtendimento extends MongoRepository<HorarioAtendimento, String> {

    List<HorarioAtendimento> findAllByOrderBySetorAscDiaSemanaAsc();

    List<HorarioAtendimento> findBySetorOrderByDiaSemanaAsc(String setor);
}
