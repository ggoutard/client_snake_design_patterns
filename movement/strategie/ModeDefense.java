package tp1progreseau.movement.strategie;

import java.util.ArrayList;

import tp1progreseau.gameElement.Snake;
import tp1progreseau.model.InputMap;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.utils.ItemType;
import tp1progreseau.utils.Position;



public class ModeDefense extends ModeStrategie {

    private Position pomme;
    private ArrayList<Position> invicibilityBalls;

    public ModeDefense(Snake snake, ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map) {
        super(snake, snakes, items, map);
        this.invicibilityBalls = new ArrayList<>(); 
        this.setMap(snake, snakes, items, map);
    }

    @Override
    public void setMap(Snake snake, ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map) {
        super.setMap(snake, snakes, items, map);

        if (this.invicibilityBalls != null) {
            this.invicibilityBalls.clear();
        } else {
            this.invicibilityBalls = new ArrayList<>(); 
        }

        for (FeaturesItem item : items) {
            if (item.getItemType() == ItemType.APPLE) {
                this.pomme = new Position(item.getX(), item.getY());
            }
            if (item.getItemType() == ItemType.INVINCIBILITY_BALL) {
                this.invicibilityBalls.add(new Position(item.getX(), item.getY()));
            }
        }
    }


    public boolean pommeHere(int x, int y)
    {
        return this.pomme.getX() == x && this.pomme.getY() == y;
    }

    public boolean invicibilityBallHere(int x, int y)
    {
        for(Position position : this.invicibilityBalls)
        {
            if(position.getX() == x 
               && position.getY() == y ) return true;
        }
        return false;
    }



    public AgentAction chooseMove() {

        
        NodeStrategie rootNode = NodeStrategie.buildRoot(this.getSnake().getPositions().getFirst().getX(), 
                                                         this.getSnake().getPositions().getFirst().getY(), 
                                                         null);
        
        NodeStrategie nodeTarget = null;
        if(this.invicibilityBalls.size() < 1 && this.pomme != null && !this.isDangerous(pomme.getX(), pomme.getY())) nodeTarget = rootNode.breadthFirstSearch(this, pomme.getX(), pomme.getY());
        else if(this.invicibilityBalls.size() != 0) {
        for (Position position : this.invicibilityBalls)
        {
            if(!this.isDangerous(position.getX(), position.getY()))
            {
                nodeTarget = rootNode.breadthFirstSearch(this, position.getX(), position.getY());
            }
        }
        }
        try {
            while (nodeTarget.getParentNode().getParentNode() != null) {
                nodeTarget = nodeTarget.getParentNode();
            }
            return nodeTarget.getMouvement(); 
        } catch (Exception e) {

                int x = this.getSnake().getPositions().getFirst().getX();
                int y = this.getSnake().getPositions().getFirst().getY();
                int sizeX  = this.getMap().getSizeX();
                int sizeY  = this.getMap().getSizeX();
    
                if (this.getSnake().getTaille() != 1) {
                    
                    if (!this.isDangerous((x + 1) % sizeX, y)) {
                        return AgentAction.MOVE_RIGHT;
                    }
                    if (!this.isDangerous((x - 1 + sizeX) % sizeX, y)) {
                        return AgentAction.MOVE_LEFT;
                    }
                    if (!this.isDangerous(x, (y - 1 + sizeY) % sizeY)) {
                        return AgentAction.MOVE_UP;
                    }
                    if (!this.isDangerous(x, (y + 1) % sizeY)) {
                        return AgentAction.MOVE_DOWN;
                    }
                }
                return null;
            }
        }

        public Position findFarthestPoint(Position head, int mapWidth, int mapHeight) {
        
            Position topLeft = new Position(0, 0);
            Position topRight = new Position(this.getSizeX() - 1, 0);
            Position bottomLeft = new Position(0, this.getSizeY() - 1);
            Position bottomRight = new Position(this.getSizeX() - 1, this.getSizeY() - 1);
        
            double maxDistance = 0;
            Position farthestPoint = topLeft;
        
            Position[] corners = {topLeft, topRight, bottomLeft, bottomRight};
        
            for (Position corner : corners) {
                double distance = Math.pow(corner.getX() - head.getX(), 2) + Math.pow(corner.getY() - head.getY(), 2);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    farthestPoint = corner;
                }
            }
        
            return farthestPoint;
        }
        
}
        








