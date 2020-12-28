package mtcg.config.filters;

import http.model.http.HttpExchange;
import http.model.interfaces.Authentication;
import http.model.interfaces.PreFilter;
import lombok.RequiredArgsConstructor;
import mtcg.service.AuthenticationService;

import java.util.Optional;

@RequiredArgsConstructor
public class BearerAuthenticationFilter implements PreFilter {

    private final AuthenticationService authenticationService;

    public void doFilter(HttpExchange exchange) {
        Optional<Authentication> principal = authenticationService.authenticateUser(exchange.getRequest().getHeaderBearerToken());
        exchange.setUser(principal);
    }
}
