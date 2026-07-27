package tp1progreseau.movement;

import java.util.ArrayList;

import tp1progreseau.gameElement.Snake;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.view.ViewSnakeGame;
import tp1progreseau.model.InputMap;


public class HumanMovement extends Movement {
    
    public HumanMovement(Snake snake)
    {        
        super(snake);

    }
    
    @Override
    public AgentAction apply(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {

        if(this.getSnake().getId() == 0) return ViewSnakeGame.ActionPlayer1;
        return ViewSnakeGame.ActionPlayer2;
    }
}
