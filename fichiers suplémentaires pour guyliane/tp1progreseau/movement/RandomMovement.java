package tp1progreseau.movement;

import tp1progreseau.gameElement.Snake;

import java.util.ArrayList;
import java.util.Random;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.model.InputMap;

public class RandomMovement extends Movement {


    public RandomMovement(Snake snake)
    {
        super(snake);

    }

    @Override
    public AgentAction apply(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {
        Random random = new Random(); 
        int counter = random.nextInt(4);


        return switch (counter) {
            case 0 -> AgentAction.MOVE_RIGHT;
            case 1 -> AgentAction.MOVE_LEFT;
            case 2 -> AgentAction.MOVE_UP;
            default -> AgentAction.MOVE_DOWN;
        };
    }

    
}
