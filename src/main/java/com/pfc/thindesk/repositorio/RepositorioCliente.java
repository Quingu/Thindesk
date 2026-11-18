package com.pfc.thindesk.repositorio;

import com.pfc.thindesk.entity.Cliente;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioCliente extends MongoRepository<Cliente, String> {

    List<Cliente> findAllByOrderByNomeAsc();

    List<Cliente> findByAtivoTrueOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}
