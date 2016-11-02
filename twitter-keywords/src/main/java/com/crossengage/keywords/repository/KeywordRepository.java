package com.crossengage.keywords.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.crossengage.keywords.domain.Keyword;

import java.util.List;
import java.util.Map;

/**
 * @author Rafael
 */
@RepositoryRestResource(collectionResourceRel = "keywords", path = "keywords")
public interface KeywordRepository extends GraphRepository<Keyword>, RelationshipOperationsRepository<Keyword> {
    // Spring figures out Neo4j query based on method name:
    Keyword findByWord(String word);

    // Spring implements method using query defined in annotation:
    @Query("START n = node(*) MATCH n-[t:Tag]->c RETURN c.word as tag, count(t) AS tagCount ORDER BY tagCount DESC limit 10")
    List<Map> findTopKeywords();
    @Query("start n=node({0}) MATCH n-[*4]-(m:Keyword) WHERE n <> m RETURN DISTINCT m LIMIT 10")
    List<Keyword> findRelevantKeywords(long keywordId);
}
