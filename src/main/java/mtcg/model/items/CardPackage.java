package mtcg.model.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import mtcg.model.interfaces.Card;
import mtcg.model.interfaces.ItemContainer;

import java.util.List;

@Builder
@Data
public class CardPackage implements ItemContainer {

    @JsonIgnore
    private final Long id;
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
