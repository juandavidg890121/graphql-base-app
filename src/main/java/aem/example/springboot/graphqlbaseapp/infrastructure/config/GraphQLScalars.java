package aem.example.springboot.graphqlbaseapp.infrastructure.config;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GraphQLScalars {

    private static boolean looksLikeAnEmailAddress(String possibleEmailValue) {
            return Pattern.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", possibleEmailValue);
    }

    @Bean
    public GraphQLScalarType email() {
        return GraphQLScalarType.newScalar()
                .name("Email")
                .description("Email field type")
                .coercing(new Coercing() {
                    @Override
                    public Object serialize(Object o) throws CoercingSerializeException {
                        String possibleEmailValue = String.valueOf(o);
                        if (looksLikeAnEmailAddress(possibleEmailValue)) {
                            return possibleEmailValue;
                        } else {
                            throw new CoercingSerializeException("Unable to serialize " + possibleEmailValue + " as an email address");
                        }
                    }

                    @Override
                    public Object parseValue(Object o) throws CoercingParseValueException {
                        if (o instanceof String) {
                            String possibleEmailValue = o.toString();
                            if (looksLikeAnEmailAddress(possibleEmailValue)) {
                                return possibleEmailValue;
                            }
                        }
                        throw new CoercingParseValueException("Unable to parse variable value " + o + " as an email address");
                    }

                    @Override
                    public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                        if (o instanceof StringValue) {
                            String possibleEmailValue = ((StringValue) o).getValue();
                            if (looksLikeAnEmailAddress(possibleEmailValue)) {
                                return possibleEmailValue;
                            }
                        }
                        throw new CoercingParseLiteralException("Value is not any email address : '" + o + "'");
                    }
                }).build();
    }
}
