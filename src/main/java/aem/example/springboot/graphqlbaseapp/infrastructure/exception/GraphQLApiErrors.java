package aem.example.springboot.graphqlbaseapp.infrastructure.exception;

import graphql.GraphQLError;
import graphql.servlet.core.GraphQLErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphQLApiErrors implements GraphQLErrorHandler {
    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> list) {
        return list;
    }
}
