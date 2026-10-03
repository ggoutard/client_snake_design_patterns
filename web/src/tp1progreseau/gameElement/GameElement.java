package tp1progreseau.gameElement;
import tp1progreseau.utils.Features;
import tp1progreseau.utils.Position;

public abstract class GameElement {
    
    private Position position;
    private Features informations;
    
    GameElement(Features informations, Position position)
    {
        this.position = position;
        this.informations = informations;
    }

    public Position getPosition() 
    {
        return position;
    }

    public Features getInformations() 
    {
        return this.informations;
    }

}
