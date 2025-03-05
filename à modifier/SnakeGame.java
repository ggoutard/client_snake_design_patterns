package tp1progreseau.model;
import tp1progreseau.gameElement.*;
import tp1progreseau.gameElement.fabrique.FabriqueItem;
import tp1progreseau.gameElement.fabrique.FabriqueSnake;
import tp1progreseau.gameElement.fabrique.TypeSnake;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import tp1progreseau.utils.*;
import tp1progreseau.view.PanelSnakeGame;
//modifier
public class SnakeGame extends Game {

    private static SnakeGame Uniqueinstance;
    
    private InputMap map;

    private ArrayList<Snake> snakes = new ArrayList<>();

    private ArrayList<Item> items = new ArrayList<>();

    private double pItem;

    private String fileName;

    private TypeSnake joueur1;
    
    private TypeSnake joueur2;

    


    private SnakeGame(String fileName, TypeSnake joueur1, TypeSnake joueur2) 
    {
        super(0);
        this.fileName = fileName;
        this.map = new InputMap(fileName);
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;

    
        this.pItem = 0;
        
    }
    
    public static SnakeGame buildGame(String fileName, TypeSnake joueur1, TypeSnake joueur2) 
    {
        if(SnakeGame.Uniqueinstance == null)
        {
            SnakeGame.Uniqueinstance = new SnakeGame(fileName,joueur1,joueur2);
               
        }
        return SnakeGame.Uniqueinstance;
    }


    
    @Override
    public void initializeGame() {

        this.map = new InputMap(this.fileName);

        this.items.clear();
        this.snakes.clear();

        FabriqueSnake.resetCounter();
        
        FabriqueSnake fabriqueSnake = new FabriqueSnake();
        FabriqueItem fabriqueItem = new FabriqueItem();
        
        switch (this.joueur1) {

            case TypeSnake.HUMAN:
            this.snakes.add((Snake)fabriqueSnake.buildHuman(this.map.getStart_snakes().getFirst()));
            break;

            case TypeSnake.RANDOM:
            this.snakes.add((Snake)fabriqueSnake.build(this.map.getStart_snakes().getFirst()));
            break;

            case TypeSnake.IA:
            this.snakes.add((Snake)fabriqueSnake.buildIA(this.map.getStart_snakes().getFirst()));
            break; 
        }

        if (this.joueur2 != null)
        {
            switch (this.joueur2) {

                case TypeSnake.HUMAN:
                this.snakes.add((Snake)fabriqueSnake.buildHuman(this.map.getStart_snakes().getLast()));
                break;

                case TypeSnake.RANDOM:
                this.snakes.add((Snake)fabriqueSnake.build(this.map.getStart_snakes().getLast()));
                break;

                case TypeSnake.IA:
                this.snakes.add((Snake)fabriqueSnake.buildIA(this.map.getStart_snakes().getLast()));
                break; 
            }
        }

        
        
        for (FeaturesItem featuresItem : this.map.getStart_items()) {
            this.items.add((Item) fabriqueItem.build(featuresItem));
        }

        this.turn = 0;
    }

    public void setMap(InputMap map) {
        this.map = map;
    }

    @Override
    public void restart()
    {
        this.turn = 0;
        this.pause();
        this.initializeGame();
        this.Launch();
    }


    public static boolean isLegalMove(Snake agent, AgentAction agentAction)
    {
        if(agent.getTaille()> 1)
        {
            if(agent.getLasAction() == AgentAction.MOVE_DOWN && AgentAction.MOVE_UP == agentAction) return false;
            if(agent.getLasAction() == AgentAction.MOVE_UP && AgentAction.MOVE_DOWN == agentAction) return false;
            if(agent.getLasAction() == AgentAction.MOVE_RIGHT && AgentAction.MOVE_LEFT == agentAction) return false;
            return !(agent.getLasAction() == AgentAction.MOVE_LEFT && AgentAction.MOVE_RIGHT == agentAction);
        }
        else return true;
    }

    @Override
    public void takeTurn() {
        
        Iterator<Snake> iteratorSnake = snakes.iterator();
        while(iteratorSnake.hasNext()) 
        {
            Snake snake = iteratorSnake.next();
            snake.moveAgent(this.MakeFeaturesSnake(), this.MakeFeaturesItem(), this.map);
            Iterator<Item> iteratorItems = items.iterator();
            while(iteratorItems.hasNext()) 
            {
                Item item = iteratorItems.next();
                item.apply(snake);
                if (item.isGrab()) iteratorItems.remove(); 
            }
            if (snake.isDead(this.map)) iteratorSnake.remove(); 
        }
        
        if(!this.pommePresent()) this.addPomme();
        
        Random rand = new Random(); 
        Double randomdouble = rand.nextDouble(1); 
        
        if (randomdouble < pItem) this.addSpecialObject();

        if (snakes.isEmpty()) 
        {
            this.thread.interrupt();
            this.pause();    
        }    

    }


    @Override
    public void step()
    {

        if(this.gameContinue())
        {
            ++this.turn;
            this.takeTurn();
            
            this.setChanged();
            this.notifyObservers();
    

            try {
                Thread.sleep((long)this.getTime()); 
            }

            catch (InterruptedException e) {
            }
        }

        else {
            super.isRunning = false;
            this.gameOver();
        }
    }

    @Override
    public boolean gameContinue(){ return true;}

    @Override
    public void gameOver()
    {
        this.pause();
    }
    

    public ArrayList<FeaturesSnake> MakeFeaturesSnake() 
    {
        ArrayList<FeaturesSnake> featuresSnakes = new ArrayList<>();

        for(Snake snake : snakes) 
        {
            featuresSnakes.add(snake.MakeFeaturesSnake());
        }
        
        return featuresSnakes;
    }

    public ArrayList<FeaturesItem> MakeFeaturesItem() 
    {
        ArrayList<FeaturesItem> featuresItem = new ArrayList<>();

        for(Item item : items) 
        {
            featuresItem.add(item.MakeFeaturesItem());
        }

        return featuresItem;
    }

    public boolean pommePresent()
    {
        for(Item item : this.items)
        {
            if (item.getClass() == ItemPomme.class) return true;
        }
        return false;
    }

    public boolean caseFree(int x,int y)
    {
        for(Snake snake : this.snakes)
        {
            for(Position position : snake.getPositions())
            {
                if(position.getX() == x && position.getY() == y) 
                {
                    return false;
                }
            }
        }
        
        for(Item item : this.items)
        {
            if(item.getPosition().getX() == x && item.getPosition().getY() == y) 
            {
                return false;
            }
        }

        return !map.get_walls()[x][y];
        
        
    }
    

    public void addPomme()
    {
        pItem += 0.05;
        FabriqueItem fabriqueItem = new FabriqueItem();
        Position caseVide = this.getfreeCase();
        this.items.add((Item) fabriqueItem.build(new FeaturesItem(caseVide.getX(),caseVide.getY(),ItemType.APPLE)));
    }

    public void addInvicibilityBall()
    {
        FabriqueItem fabriqueItem = new FabriqueItem();
        Position caseVide = this.getfreeCase();
        this.items.add((Item) fabriqueItem.build(new FeaturesItem(caseVide.getX(),caseVide.getY(),ItemType.INVINCIBILITY_BALL)));
    }
    
    
    public void addSickBall()
    {
        FabriqueItem fabriqueItem = new FabriqueItem();
        Position caseVide = this.getfreeCase();
        this.items.add((Item) fabriqueItem.build(new FeaturesItem(caseVide.getX(),caseVide.getY(),ItemType.SICK_BALL)));
    }

        
    public void addBox()
    {
        FabriqueItem fabriqueItem = new FabriqueItem();
        Position caseVide = this.getfreeCase();
        this.items.add((Item) fabriqueItem.build(new FeaturesItem(caseVide.getX(),caseVide.getY(),ItemType.BOX)));
    }

    public void addSpecialObject()
    {
        this.pItem = 0;
        Random rand = new Random(); 
        int randomInteger = rand.nextInt(3); 
        if (randomInteger == 0) this.addInvicibilityBall();
        if (randomInteger == 1) this.addSickBall();
        if (randomInteger == 2) this.addBox();
    }



    public Position getfreeCase(){
        Random random = new Random(); 
        int x = random.nextInt(map.getSizeX());
        int y = random.nextInt(map.getSizeY());
        while (!this.caseFree(x, y))
        {
            x = random.nextInt(map.getSizeX());
            y = random.nextInt(map.getSizeY());
        }
        return new Position(x,y);
    }
    
    public int getX()
    {
    
    	return this.map.getSizeX();
    
    }
    
    public int getY()
    {
    
    	return this.map.getSizeY();
 
    }
    
    public boolean[][] getWalls()
    {
    	return this.getWalls();
    }
}
