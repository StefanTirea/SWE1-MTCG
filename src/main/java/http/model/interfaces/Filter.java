package http.model.interfaces;

import http.model.http.HttpExchange;

public interface Filter {

    void doFilter(HttpExchange exchange);
}
