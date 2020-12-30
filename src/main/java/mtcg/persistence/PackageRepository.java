package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.entity.PackageEntity;
import mtcg.model.interfaces.Card;
import mtcg.model.items.CardPackage;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageRepository extends BaseRepository<PackageEntity> {

    private final PackagedItemsRepository packagedItemsRepository;
    private final CardRepository cardRepository;

    public PackageRepository(ConnectionPool connectionPool,
                             PackagedItemsRepository packagedItemsRepository,
                             CardRepository cardRepository) {
        super(connectionPool, PackageEntity.class);
        this.packagedItemsRepository = packagedItemsRepository;
        this.cardRepository = cardRepository;
    }

    public List<CardPackage> getPackagesByUser(Long userId) {
        return getEntitiesByFilter("user_id", userId).stream()
                .map(entity -> CardPackage.builder()
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .content(packagedItemsRepository.getItemsInPackage(entity.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public boolean savePackage(CardPackage cardPackage, Long userId) {
        Long packageId = insert(PackageEntity.builder()
                .userId(userId)
                .name(cardPackage.getName())
                .description(cardPackage.getDescription())
                .build());
        List<Long> cardIds = cardRepository.saveBattleCardWithoutUser(cardPackage.getContent());
        return packagedItemsRepository.insertCardInPackage(cardIds, packageId);
    }
}
