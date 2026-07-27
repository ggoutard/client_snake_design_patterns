package tp1progreseau.gameElement.fabrique;

import tp1progreseau.gameElement.GameElement;
import tp1progreseau.utils.Features;

public interface FabriqueGameElement {
    
    public abstract GameElement build(Features features);

}
