package mtcg.config.filter;

import http.model.http.HttpExchange;
import http.model.interfaces.Authentication;
import http.model.interfaces.PreFilter;
import lombok.RequiredArgsConstructor;
import mtcg.service.UserService;

import java.util.Optional;

@RequiredArgsConstructor
public class BearerAuthenticationFilter implements PreFilter {

    private final UserService userService;

    public void doFilter(HttpExchange exchange) {
        Optional<Authentication> principal = userService.authenticateUser(exchange.getRequest().getHeaderBearerToken());
        exchange.setUser(principal);
    }
}
