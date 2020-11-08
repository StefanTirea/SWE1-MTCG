package mtcg.model.items;

import mtcg.model.interfaces.Card;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mtcg.model.fixture.CardsFixture.cardPackage;
import static mtcg.model.fixture.CardsFixture.monsterCard;
import static mtcg.model.fixture.CardsFixture.spellCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardPackageTest {

    @Test
    void open_packageWithCards_returnsCards() {
        CardPackage cardPackage = cardPackage();
        List<Card> openedCards = cardPackage.open();

        assertThat(cardPackage.open()).isEmpty();
        assertThat(openedCards)
                .containsExactly(monsterCard(), spellCard());
    }

    @Test
    void open_packageNull_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> CardPackage.builder().build());
    }
}
