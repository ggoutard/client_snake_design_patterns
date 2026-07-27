package tp1progreseau.movement;

import java.util.ArrayList;

import tp1progreseau.gameElement.Snake;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.model.InputMap;


public abstract class Movement {

    private final  Snake snake;

    public Movement(Snake snake)
    {
        this.snake = snake;
    }

    public Snake getSnake()
    {
        return this.snake;
    }

    public abstract AgentAction apply(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map);
    
}
