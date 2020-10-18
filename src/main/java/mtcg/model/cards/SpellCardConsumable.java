package mtcg.model.cards;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.Consumable;

@Builder
@Data
@AllArgsConstructor
public class SpellCardConsumable implements Card, Consumable {

    private String name;
    private String description;

    @Override
    public void consume() {
        throw new UnsupportedOperationException();
    }
}
