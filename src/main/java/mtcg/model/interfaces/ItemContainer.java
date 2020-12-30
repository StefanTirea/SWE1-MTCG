package mtcg.model.interfaces;

import java.util.List;

/**
 * An Item Container holds 1-n Tradable/Non-Tradable Items
 * These items are not visible to the player!
 * <p>
 * -) Can be opened and the items in the container are transferred to the user inventory
 */
public interface ItemContainer extends Item, Tradable {

    String getDescription();

    List<? extends Item> open();
}
