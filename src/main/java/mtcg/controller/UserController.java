package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Post;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import mtcg.persistence.UserRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
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

    @Post("/api/sessions")
    public Optional<String> loginUser(@RequestBody UserData userData) {
        if (userData.valid()) {
            return userRepository.loginUser(userData);
        } else {
            throw new BadRequestException("username & password must not be blank!");
        }
    }
}
