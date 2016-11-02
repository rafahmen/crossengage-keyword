package com.crossengage.keywords;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.rest.SpringCypherRestGraphDatabase;

/**
 *@author Rafael
 */
@Configuration
public class Neo4JConfig {

    @Bean(destroyMethod = "shutdown")
    // Param injected from application.properties file:
    public GraphDatabaseService graphDatabaseService(final @Value("${neo4j.uri}") String neo4jUri) {
        return new SpringCypherRestGraphDatabase(neo4jUri);
    }
}
