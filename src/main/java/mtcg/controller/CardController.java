package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import lombok.RequiredArgsConstructor;
import mtcg.model.user.User;
import mtcg.persistence.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Secured
@RequiredArgsConstructor
public class CardController {

    private final UserRepository userRepository;

    @Post("/api/decks")
    public boolean createDeck(User user, @RequestBody List<Number> cardIds) {
        List<Long> ids = cardIds.stream()
                .distinct()
                .filter(Objects::nonNull)
                .map(Number::longValue)
                .collect(Collectors.toList());
        boolean result = user.createDeck(ids);
        userRepository.updateUser(user);
        return result;
    }
}
