package mtcg.model.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.ItemContainer;

import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
public class CardPackage implements ItemContainer {

    @Getter
    private final String name;
    @Getter
    private final String description;
    @NonNull
    @JsonIgnore
    private final List<Card> content;

    public List<Card> open() {
        synchronized (content) {
            List<Card> cards = List.copyOf(content);
            content.clear();
            return cards;
        }
    }
}
