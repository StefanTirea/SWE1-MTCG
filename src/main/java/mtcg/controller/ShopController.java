package mtcg.controller;

import http.model.annotation.Controller;
import http.model.annotation.Post;
import http.model.annotation.Secured;
import http.model.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import mtcg.model.items.CardPackage;
import mtcg.model.user.User;
import mtcg.persistence.UserRepository;
import mtcg.service.card.CardGenerator;

@Controller
@RequiredArgsConstructor
@Secured
public class ShopController {

    private final UserRepository userRepository;
    private final CardGenerator cardGenerator;

    @Post("/api/transactions/packages")
    public CardPackage buyPackage(User user) {
        if (user.spentCoins(5)) {
            CardPackage cardPackage = cardGenerator.generateCardPackage(5);
            user.addItem(cardPackage);
            userRepository.updateUser(user);
            return cardPackage;
        } else {
            throw new BadRequestException("Not enough money! 5 coins required, you have " + user.getCoins());
        }
    }
}
