package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.PackagedItemsEntity;
import mtcg.model.interfaces.BattleCard;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackagedItemsRepository extends BaseRepository<PackagedItemsEntity> {

    private final CardRepository cardRepository;

    public PackagedItemsRepository(CardRepository cardRepository) {
        super(PackagedItemsEntity.class);
        this.cardRepository = cardRepository;
    }

    public List<BattleCard> getItemsInPackage(Long packageId) {
        return cardRepository.getBattleCardsByIds(selectEntitiesByFilter("package_id", packageId).stream()
                .map(PackagedItemsEntity::getCardId)
                .collect(Collectors.toList()));
    }

    public boolean insertCardInPackage(List<Long> cardIds, Long packageId) {
        cardIds.forEach(cardId -> insert(PackagedItemsEntity.builder()
                .cardId(cardId)
                .packageId(packageId)
                .build()));
        return true;
    }
}
