package com.pfc.thindesk.repositorio;

import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioUsuario extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findAllByOrderByNomeAsc();

    List<Usuario> findByPerfilOrderByNomeAsc(Perfil perfil);

    long countByPerfil(Perfil perfil);

    long countByAtivoTrue();
}
