package com.crossengage.keywords.domain;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * @author Rafael
 */
@RelationshipEntity(type = "Mention")
public class Mention {
    @GraphId
    Long id;
    @StartNode
    public Tweet tweet;
    @EndNode
    public Profile profile;
}