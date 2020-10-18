package mtcg.model.interfaces;

/**
 * An Item Container holds 1-n Tradable/Non-Tradable Items
 * These items are not visible to the player!
 * <p>
 * -) Can be opened and the items in the container are transferred to the user inventory
 * Note: This interface has not the Tradable Interface to open up the possibility of non tradable containers
 */
public interface ItemContainer extends Item, Tradable {

    String getName();

    String getDescription();
}
