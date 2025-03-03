package tp1progreseau.gameElement;


import tp1progreseau.utils.Features;
import tp1progreseau.utils.Position;

public class ItemSickBall extends Item {

    public ItemSickBall(Features informations, Position position)
    {
        super(informations,position);
    } 

    @Override
    public void apply(Snake snake)
    {
        if (this.grabBy(snake))
        {
           snake.becomeSick();
        }
    }

    
}
