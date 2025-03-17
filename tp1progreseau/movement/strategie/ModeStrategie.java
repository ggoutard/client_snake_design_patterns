package tp1progreseau.movement.strategie;
import java.util.ArrayList;
import tp1progreseau.gameElement.Snake;
import tp1progreseau.model.InputMap;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.utils.ItemType;
import tp1progreseau.utils.Position;

public abstract class ModeStrategie {

    private Snake snake;
    private int idSnakeEnnemi;
    private InputMap map;
    private ArrayList<FeaturesSnake> snakes;
    private ArrayList<Position> sickBalls;
    private ArrayList<Position> boxs;



    public ModeStrategie(Snake snake,ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {   
        this.setMap(snake,snakes, items, map);
        if(snake.getId() == 1) this.idSnakeEnnemi = 0;
        else this.idSnakeEnnemi = 1;
        this.sickBalls = new ArrayList<>();
        this.boxs = new ArrayList<>();
        this.setMap(snake, snakes, items, map);
        
    }

    public InputMap getMap() {
        return map;
    }

    public ArrayList<Position> getPositionsEnnemi()
    {
        return this.snakes.get(this.idSnakeEnnemi).getPositions();
    }

    public ArrayList<Position> getMyPositions()
    {
        return this.snakes.get(snake.getId()).getPositions();
    }

    public boolean isWall(int x, int y)
    {
        return this.map.get_walls()[x][y];
    }

    public void setMap(Snake snake,ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
    {
        this.snake = snake;
        this.map = map;
        this.snakes = snakes;
        this.sickBalls = new ArrayList<>();
        this.boxs = new ArrayList<>();

        this.sickBalls.clear();
        this.boxs.clear();
        for (FeaturesItem item : items) 
        {
            if (item.getItemType() == ItemType.SICK_BALL) 
            {
                this.sickBalls.add(new Position(item.getX(), item.getY()));
            }
            if (item.getItemType() == ItemType.BOX) 
            {
                this.boxs.add(new Position(item.getX(), item.getY()));
            }

        }
    }
    
    public boolean SnakeSick()
    {
        return snake.isSick();
    }

    public boolean SnakeInvisible()
    {
        return snake.isInvisible();
    }

    public boolean isDangerous(int x, int y) {
        if (this.snakes.size() > 1) {
            return this.isWall(x, y) 
                || this.isInPositions(x, y, this.getMyPositions()) 
                || this.isInPositions(x, y, this.getPositionsEnnemi());
                //|| this.isInPositions(x, y, this.sickBalls)
                //|| this.isInPositions(x, y, this.boxs);

        } else {
            return this.isWall(x, y) 
                || this.isInPositions(x, y, this.getMyPositions());
                //|| this.isInPositions(x, y, this.sickBalls)
                //|| this.isInPositions(x, y, this.boxs);
        }
    }
    
    private boolean isInPositions(int x, int y, ArrayList<Position> positions) {
        for (Position p : positions) {
            if (p.getX() == x && p.getY() == y) {
                return true;
            }
        }
        return false;
    }
    

    public Snake getSnake() {
        return snake;
    }

    public int getSizeX(){
        return this.map.getSizeX();
    }

    public int getSizeY(){
        return this.map.getSizeY();
    }

    public abstract AgentAction chooseMove();
    
}
