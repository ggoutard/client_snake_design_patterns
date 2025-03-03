package tp1progreseau.gameElement;
import java.util.Random;

import tp1progreseau.utils.Features;
import tp1progreseau.utils.Position;

public class ItemBox extends Item {

    private Item reelItem;

    public ItemBox(Features informations, Position position)
    {
        super(informations, position);

        Random rand = new Random(); 

        int randomInteger = rand.nextInt(2)%2; 

        if (randomInteger == 1) {
            reelItem = new ItemInvicibilityBall(informations, position);
        } else {
            reelItem = new ItemSickBall(informations, position);
        }


    } 

    @Override
    public void apply(Snake snake)
    {
        if(super.grabBy(snake))
        {
            reelItem.apply(snake);
        }
    }
}