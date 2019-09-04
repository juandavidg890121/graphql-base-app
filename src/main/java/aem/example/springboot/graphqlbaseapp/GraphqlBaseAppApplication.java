package aem.example.springboot.graphqlbaseapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GraphqlBaseAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlBaseAppApplication.class, args);
    }

}
