package tp1progreseau.gameElement;
import tp1progreseau.utils.Features;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.Position;

public abstract class Item extends GameElement {

    private boolean grab;
    public Item(Features informations, Position position)
    {
        super(informations,position);
        this.grab = false;
    } 

    public FeaturesItem MakeFeaturesItem()
	{
		FeaturesItem featuresItem = (FeaturesItem) super.getInformations();
		return featuresItem;
	}

    public abstract void apply(Snake snake);

    public boolean grabBy(Snake snake)
    {
        if(!snake.isSick()) this.grab = snake.getPosition().getX() == this.getPosition().getX() && snake.getPosition().getY() == this.getPosition().getY();
        return this.grab;
        
    }

    public boolean isGrab()
    {
        return this.grab;
    }
}
