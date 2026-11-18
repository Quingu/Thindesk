package com.pfc.thindesk.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Order(1)
public class MongoInitConfig implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoInitConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        criarColecaoSeAusente("usuarios");
        criarColecaoSeAusente("chamados");
        criarColecaoSeAusente("clientes");
        criarColecaoSeAusente("horariosAtendimento");
        criarColecaoSeAusente("sessoes");
    }

    private void criarColecaoSeAusente(String nome) {
        if (!mongoTemplate.collectionExists(nome)) {
            mongoTemplate.createCollection(nome);
        }
    }
}
