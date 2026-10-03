package tp1progreseau.movement.strategie;
import java.util.ArrayList;
import tp1progreseau.gameElement.Snake;
import tp1progreseau.model.InputMap;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;


public class Strategie {
    
    TypeStrategie typeStrategie;

    public Strategie()
    {
        this.typeStrategie = TypeStrategie.DEFENSE;
    }

    public AgentAction chooseMove(Snake snake, ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {
        ModeStrategie strategie;
        int id_target = 0;
        if (snake.getId() == id_target) id_target +=1; 
        if(snakes.size() > 1)
        {
            if(snakes.get(snake.getId()).getPositions().size() >  snakes.get(id_target).getPositions().size() && snakes.get(id_target).getPositions().size() != 1 ) strategie = new ModeAttaque(snake,snakes,items,map);
            else strategie = new ModeDefense(snake,snakes,items,map);
        }
        else strategie = new ModeDefense(snake,snakes,items,map);
        return strategie.chooseMove();
    }
    
}
