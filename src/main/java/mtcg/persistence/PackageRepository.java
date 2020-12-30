package mtcg.persistence;

import http.model.annotation.Component;
import mtcg.model.cards.MonsterCard;
import mtcg.model.cards.SpellCardAttacking;
import mtcg.model.entity.CardEntity;
import mtcg.model.entity.PackageEntity;
import mtcg.model.interfaces.BattleCard;
import mtcg.persistence.base.BaseRepository;
import mtcg.persistence.base.ConnectionPool;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageRepository extends BaseRepository<PackageEntity> {

    public PackageRepository(ConnectionPool connectionPool) {
        super(connectionPool, PackageEntity.class);
    }

    public List<PackageEntity> getPackagesByUser(Long userId) {
        return getEntitiesByFilter("user_id", userId);
    }
}
