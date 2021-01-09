package mtcg.model.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import mtcg.model.interfaces.BattleCard;

@Builder
@Data
public class TradingOffer {

    private Long id;
    @JsonProperty
    private Long cardId;
    @JsonIgnore
    private BattleCard card;
    private Integer minDamage;
    private String type;

    @JsonGetter
    public BattleCard getCard() {
        return card;
    }

    @JsonIgnore
    public Long getCardId() {
        return cardId;
    }
}
