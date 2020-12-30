package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.PackagedItemsEntity;
import mtcg.model.interfaces.BattleCard;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackagedItemsRepository extends BaseRepository<PackagedItemsEntity> {

    private final CardRepository cardRepository;

    public PackagedItemsRepository(ConnectionPool connectionPool, CardRepository cardRepository) {
        super(connectionPool, PackagedItemsEntity.class);
        this.cardRepository = cardRepository;
    }

    public List<BattleCard> getItemsInPackage(Long packageId) {
        return cardRepository.getBattleCardsByIds(getEntitiesByFilter("package_id", packageId).stream()
                .map(PackagedItemsEntity::getCardId)
                .collect(Collectors.toList()));
    }
}
