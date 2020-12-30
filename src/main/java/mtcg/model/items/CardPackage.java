package mtcg.model.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import mtcg.model.entity.CardEntity;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.ItemContainer;

import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class CardPackage implements ItemContainer {

    private final String name;
    private final String description;
    @NonNull
    @JsonIgnore
    private final List<? extends Card> content;

    public List<Card> open() {
        synchronized (content) {
            List<Card> cards = List.copyOf(content);
            content.clear();
            return cards;
        }
    }
}
