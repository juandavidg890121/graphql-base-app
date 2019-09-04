package aem.example.springboot.graphqlbaseapp.infrastructure.config.security.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class GraphQLSecure {

    @Pointcut("target(com.coxautodev.graphql.tools.GraphQLResolver)")
    private void allGraphQLResolverMethods() {
    }

    @Pointcut("within(aem.example.springboot.graphqlbaseapp..*)")
    private void isDefinedInApp() {
    }

    @Pointcut("@annotation(aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public)")
    private void isPublicApi() {
    }

    @Before("allGraphQLResolverMethods() && isDefinedInApp() && !isPublicApi()")
    public void check() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                || AnonymousAuthenticationToken.class.isAssignableFrom(SecurityContextHolder.getContext().getAuthentication().getClass())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
