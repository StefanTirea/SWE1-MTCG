package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Post;
import http.model.annotation.Put;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.enums.HttpStatus;
import http.model.exception.BadRequestException;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import mtcg.persistence.UserRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Get("/api/users")
    @Secured
    public User showUser(User user) {
        return user;
    }

    @Post("/api/users")
    public boolean registerUser(@RequestBody UserData userData) {
        if (userData.valid()) {
            return userRepository.createUser(userData);
        } else {
            throw new BadRequestException("username & password must not be blank!");
        }
    }

    @Put("/api/users")
    @Secured
    public HttpResponse editUser(User user, @RequestBody UserData userData) {
        if (userData.valid()) {
            if (user.getUsername().equals(userData.getUsername())
                    || userRepository.getEntitiesByFilter("username", userData.getUsername()).isEmpty()) {
                userRepository.updateUserCredentials(user.getId(), userData.getUsername(), userData.getPassword());
                return HttpResponse.builder().httpStatus(HttpStatus.OK).build();
            }
        }
        return HttpResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).build();
    }

    @Post("/api/sessions")
    public HttpResponse loginUser(@RequestBody UserData userData) {
        if (userData.valid()) {
            return userRepository.loginUser(userData);
        } else {
            throw new BadRequestException("username & password must not be blank!");
        }
    }
}
