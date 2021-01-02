package mtcg.service;

import http.model.annotation.Component;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.http.HttpResponse;
import http.model.interfaces.Authentication;
import lombok.RequiredArgsConstructor;
import mtcg.model.entity.UserEntity;
import mtcg.model.interfaces.Item;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import mtcg.persistence.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ItemService itemService;

    public Optional<Authentication> authenticateUser(String token) {
        Optional<UserEntity> user = userRepository.getUserByToken(token);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Item> items = itemService.getInventoryByUser(user.get().getId());
        return Optional.of(User.builder()
                .id(user.get().getId())
                .username(user.get().getUsername())
                .role(user.get().getRole())
                .inventory(items)
                .coins(user.get().getCoins())
                .elo(user.get().getElo())
                .gamesPlayed(user.get().getGamesPlayed())
                .gamesWon(user.get().getGamesWon())
                .deck(itemService.getDeck(Arrays.asList(user.get().getDeck()), items))
                .build());
    }

    public boolean registerUser(UserData userData) {
        if (userData.valid()) {
            return userRepository.createUser(userData);
        } else {
            throw new BadRequestException("username & password must not be blank!");
        }
    }

    public HttpResponse editUser(User user, UserData userData) {
        if (userData.valid() && (!user.getUsername().equals(userData.getUsername())
                || userRepository.getEntitiesByFilter("username", userData.getUsername()).isEmpty())) {
            userRepository.updateUserCredentials(user.getId(), userData.getUsername(), userData.getPassword());
            return HttpResponse.builder().httpStatus(HttpStatus.OK).build();
        }
        return HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build();
    }

    public HttpResponse loginUser(UserData userData) {
        if (userData.valid()) {
            return userRepository.loginUser(userData);
        } else {
            throw new BadRequestException("username & password must not be blank!");
        }
    }
}
