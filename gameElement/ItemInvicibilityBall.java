package tp1progreseau.gameElement;

import tp1progreseau.utils.Features;
import tp1progreseau.utils.Position;

public class ItemInvicibilityBall extends Item {

    public ItemInvicibilityBall(Features informations, Position position)
    {
        super(informations,position);
    } 

    @Override
    public void apply(Snake snake)
    {
        if (this.grabBy(snake))
        {
            snake.becomeInvisible();
        }
    }


}
