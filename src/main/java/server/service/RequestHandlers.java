package server.service;

import org.apache.commons.lang3.tuple.Pair;
import server.controller.ErrorController;
import server.controller.TestController;
import server.model.HttpExchange;
import server.model.HttpMethod;
import server.model.HttpResponse;
import server.model.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static server.model.HttpMethod.DELETE;
import static server.model.HttpMethod.GET;
import static server.model.HttpMethod.POST;

public class RequestHandlers {

    private final Map<String, Map<HttpMethod, Function<HttpExchange, HttpResponse>>> handlers = new HashMap<>();
    private final TestController testController = new TestController();

    public RequestHandlers() {
        registerHandlers();
    }

    public HttpResponse getHandlerOrThrow(HttpExchange exchange) {
        String matchingPath = exchange.getRequest().getPath();
        HttpMethod method = exchange.getRequest().getHttpMethod();
        Optional<Pair<String, Function<HttpExchange, HttpResponse>>> fnPair = handlers.entrySet().stream()
                .filter(entry -> matchingPath.matches(entry.getKey()))
                .filter(entry -> entry.getValue().containsKey(method))
                .map(entry -> Pair.of(entry.getKey(), entry.getValue().get(method)))
                .findFirst();

        if (fnPair.isPresent()) {
            String[] myPath = "/messages/{id}".split("/");
            String[] requestPath = matchingPath.split("/");
            List<Object> variables = new ArrayList<>();
            for (int i = 0; i < myPath.length; i++) {
                if (myPath[i].startsWith("{")) {
                    variables.add(requestPath[i]);
                }
            }
            exchange.getRequest().setPathVariables(variables);
        }

        return fnPair
                .map(Pair::getRight)
                .map(fn -> fn.apply(exchange))
                .orElse(ErrorController.getNotFoundError());
    }

    private void registerHandlers() {
        addRequestHandler("/", GET, testController::helloWorld);
        addRequestHandler("/messages", GET, testController::getMessages);
        addRequestHandler("/messages/{id}", DELETE, testController::deleteMessage);
        addRequestHandler("/messages", POST, testController::createMessage);
        addRequestHandler("/messages", DELETE, testController::createMessage);
    }

    public static String getRegex(String path) {
        List<String> pathVariables = Arrays.stream(path.split("/"))
                .filter(line -> line.startsWith("{"))
                .collect(Collectors.toList());
        String regex = "^" + path.replace("/", "\\/") + "$";
        for (String variable : pathVariables) {
            regex = regex.replace(variable, "[^/]*");
        }
        return regex;
    }

    private void addRequestHandler(String path, HttpMethod verb, Function<HttpExchange, HttpResponse> fn) {
        if (!handlers.containsKey(getRegex(path))) {
            handlers.put(getRegex(path), new HashMap<>());
        }
        handlers.get(getRegex(path)).put(verb, fn);
    }
}
