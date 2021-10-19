package org.chillout.graphql;

import java.io.File;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Graphql {
  String schema = "type Message {" + "id: String" + "content: String" + "author: String" + "server: String}"
      + "type Query{AddMessage:Message}";

  SchemaParser schemaParser = new SchemaParser();
  TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

  public void execute(String name, Object data) {
    RuntimeWiring runtimeWiring = newRuntimeWiring()
        .type("Query", builder -> builder.dataFetcher(name, new StaticDataFetcher(data))).build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
    ExecutionResult executionResult = build.execute("{id}");

    System.out.println(executionResult.getData().toString());
  }

  public Graphql() {
  }
}
