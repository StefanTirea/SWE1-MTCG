package http.model.interfaces;

import http.model.http.HttpExchange;

public interface PreFilter extends Filter {

    void doFilter(HttpExchange exchange);
}
