package tp1progreseau.gameElement.fabrique;

import tp1progreseau.gameElement.*;
import tp1progreseau.utils.Features;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.ItemType;
import tp1progreseau.utils.Position;

public class FabriqueItem implements FabriqueGameElement {
    
    @Override
    public GameElement build(Features features)
    {
        FeaturesItem featuresItem = (FeaturesItem) features;

        if (featuresItem.getItemType() == ItemType.APPLE)
        {
            return new ItemPomme(
                features,
                new Position(featuresItem.getX(),featuresItem.getY())
            );
        }

        if (featuresItem.getItemType() == ItemType.INVINCIBILITY_BALL)
        {
            return new ItemInvicibilityBall(
                features,
                new Position(featuresItem.getX(),featuresItem.getY())
            );
        }

        if (featuresItem.getItemType() == ItemType.SICK_BALL)
        {
            return new ItemSickBall(
                features,
                new Position(featuresItem.getX(),featuresItem.getY())
            );
        }

        if (featuresItem.getItemType() == ItemType.BOX)
        {
            return new ItemBox(
                features,
                new Position(featuresItem.getX(),featuresItem.getY())
            );
        }



        return null;
    }
    
}
