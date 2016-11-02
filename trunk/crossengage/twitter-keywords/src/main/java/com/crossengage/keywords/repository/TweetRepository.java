package com.crossengage.keywords.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.crossengage.keywords.domain.Keyword;
import com.crossengage.keywords.domain.Tweet;

/**
 * @author Rafael
 */
@RepositoryRestResource(collectionResourceRel = "tweets", path = "tweets")
public interface TweetRepository extends GraphRepository<Tweet>, RelationshipOperationsRepository<Tweet> {
}
