package tp1progreseau.movement;

import tp1progreseau.gameElement.Snake;

import java.util.ArrayList;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.model.InputMap;
import tp1progreseau.movement.strategie.Strategie;


public class AiMovement extends Movement {
    
    private Strategie strategie;

    public AiMovement(Snake snake)
    {
        super(snake);
        strategie = new Strategie();
        
    }
    
    @Override
    public AgentAction apply(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {
        return this.strategie.chooseMove(super.getSnake(),snakes,items,map);
    }
}
