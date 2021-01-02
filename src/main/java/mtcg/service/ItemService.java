package mtcg.service;

import http.model.annotation.Component;
import http.model.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.Item;
import mtcg.model.items.CardPackage;
import mtcg.model.user.User;
import mtcg.persistence.CardRepository;
import mtcg.persistence.PackageRepository;
import mtcg.persistence.UserRepository;
import mtcg.service.card.CardGenerator;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemService {

    private final CardRepository cardRepository;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final CardGenerator cardGenerator;

    public List<Item> getInventoryByUser(Long userId) {
        return ListUtils.union(cardRepository.getBattleCardsByUser(userId), packageRepository.getPackagesByUser(userId));
    }

    public List<BattleCard> getDeck(List<Long> deck, List<Item> items) {
        return items.stream()
                .filter(item -> item instanceof BattleCard)
                .map(item -> (BattleCard) item)
                .filter(item -> deck.contains(item.getId()))
                .collect(Collectors.toList());
    }

    public boolean createDeck(User user, List<Number> cardIds) {
        List<Long> ids = cardIds.stream()
                .distinct()
                .filter(Objects::nonNull)
                .map(Number::longValue)
                .collect(Collectors.toList());
        boolean result = user.createDeck(ids);
        userRepository.updateUser(user);
        return result;
    }

    public List<Card> openPackage(User user) {
        Pair<Long, List<Long>> container = user.openItemContainer();
        packageRepository.delete(container.getLeft());
        cardRepository.updateBattleCards(container.getRight(), user.getId());
        return user.getStack().stream()
                .filter(item -> container.getRight().contains(item.getId()))
                .collect(Collectors.toList());
    }

    public CardPackage buyPackage(User user) {
        if (user.spentCoins(5)) {
            CardPackage cardPackage = cardGenerator.generateCardPackage(5);
            user.addItem(cardPackage);
            userRepository.updateUser(user);
            packageRepository.savePackage(cardPackage, user.getId());
            return cardPackage;
        } else {
            throw new BadRequestException("Not enough money! 5 coins required, you have " + user.getCoins());
        }
    }
}
