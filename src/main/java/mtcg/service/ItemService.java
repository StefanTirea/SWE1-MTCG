package mtcg.service;

import http.model.annotation.Component;
import lombok.RequiredArgsConstructor;
import mtcg.model.interfaces.BattleCard;
import mtcg.model.interfaces.Item;
import mtcg.persistence.CardRepository;
import mtcg.persistence.PackageRepository;
import mtcg.persistence.PackagedItemsRepository;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemService {

    private final CardRepository cardRepository;
    private final PackageRepository packageRepository;
    private final PackagedItemsRepository packagedItemsRepository;

    public List<Item> getInventoryByUser(Long userId) {
        return ListUtils.union(cardRepository.getBattleCardsByUser(userId), packageRepository.getPackagesByUser(userId).stream()
                .flatMap(packageEntity -> packagedItemsRepository.getItemsInPackage(packageEntity.getId()).stream())
                .collect(Collectors.toList()));
    }

    public List<BattleCard> getDeck(List<Long> deck, List<Item> items) {
        return items.stream()
                .filter(item -> item instanceof BattleCard)
                .map(item -> (BattleCard) item)
                .filter(item -> deck.contains(item.getId()))
                .collect(Collectors.toList());
    }
}
