package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Get;
import http.model.annotation.Post;
import http.model.annotation.Put;
import http.model.annotation.RequestBody;
import http.model.annotation.Secured;
import http.model.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import mtcg.model.user.User;
import mtcg.model.user.UserData;
import mtcg.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Get("/api/users")
    @Secured
    public User showUser(User user) {
        return user;
    }

    @Post("/api/users")
    public boolean registerUser(@RequestBody UserData userData) {
        return userService.registerUser(userData);
    }

    @Put("/api/users")
    @Secured
    public HttpResponse editUser(User user, @RequestBody UserData userData) {
        return userService.editUser(user, userData);
    }

    @Post("/api/sessions")
    public HttpResponse loginUser(@RequestBody UserData userData) {
        return userService.loginUser(userData);
    }
}
