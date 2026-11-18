package com.pfc.thindesk.servico;

import com.pfc.thindesk.dto.HorarioAtendimentoDTO;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.excecao.RecursoNaoEncontradoException;
import com.pfc.thindesk.repositorio.RepositorioHorarioAtendimento;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicoHorarioAtendimento {

    private final RepositorioHorarioAtendimento repositorioHorario;

    public ServicoHorarioAtendimento(RepositorioHorarioAtendimento repositorioHorario) {
        this.repositorioHorario = repositorioHorario;
    }

    public List<HorarioAtendimento> listarHorarios() {
        return repositorioHorario.findAllByOrderBySetorAscDiaSemanaAsc();
    }

    public HorarioAtendimento salvar(HorarioAtendimentoDTO dados) {
        HorarioAtendimento horario = new HorarioAtendimento();
        horario.setSetor(dados.getSetor().trim());
        horario.setDiaSemana(dados.getDiaSemana().trim());
        horario.setHorarioInicio(dados.getHorarioInicio().trim());
        horario.setHorarioFim(dados.getHorarioFim().trim());
        return repositorioHorario.save(horario);
    }

    public void excluir(String id) {
        HorarioAtendimento horario = repositorioHorario.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horário não encontrado."));
        repositorioHorario.delete(horario);
    }
}
