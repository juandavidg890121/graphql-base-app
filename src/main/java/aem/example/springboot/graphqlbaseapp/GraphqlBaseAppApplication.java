package aem.example.springboot.graphqlbaseapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties({LiquibaseProperties.class})
public class GraphqlBaseAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlBaseAppApplication.class, args);
    }

}
