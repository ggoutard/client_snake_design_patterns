package tp1progreseau.movement.strategie;

import java.util.ArrayList;

import tp1progreseau.gameElement.Snake;
import tp1progreseau.model.InputMap;
import tp1progreseau.utils.AgentAction;
import tp1progreseau.utils.FeaturesItem;
import tp1progreseau.utils.FeaturesSnake;
import tp1progreseau.utils.Position;

public class ModeAttaque extends ModeStrategie {

    private ArrayList<Position> otherSnake;

    public ModeAttaque(Snake snake, ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map) {
        super(snake, snakes, items, map);
        this.otherSnake = new ArrayList<>(); 
        this.setMap(snake, snakes, items, map);
    }

    public void setMap(Snake snake, ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map) {
        super.setMap(snake, snakes, items, map);

        if (snake.getId() == 0) {
            this.otherSnake = snakes.get(1).getPositions();
        } else {
            this.otherSnake = snakes.get(0).getPositions();
        }
    }

    public double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public AgentAction chooseMove() {
        NodeStrategie rootNode = NodeStrategie.buildRoot(this.getSnake().getPositions().getFirst().getX(), 
                                                         this.getSnake().getPositions().getFirst().getY(), 
                                                         null);
        
        Position head = this.getSnake().getPositions().getFirst();
        Position target = null;
        double minDistance = Double.MAX_VALUE;

        for (int i = 1; i < this.getSnake().getPositions().size(); i++) {
            Position pos = this.getSnake().getPositions().get(i);
            double distance = calculateDistance(head.getX(), head.getY(), pos.getX(), pos.getY());
            if (distance < minDistance) {
                minDistance = distance;
                target = pos;
            }
        }

        NodeStrategie nodeTarget = null;
        if (target != null) {
            nodeTarget = rootNode.breadthFirstSearch(this, target.getX(), target.getY());
        }

        try {
            if (nodeTarget != null) {
                while (nodeTarget.getParentNode().getParentNode() != null) {
                    nodeTarget = nodeTarget.getParentNode();
                }
                return nodeTarget.getMouvement(); 
            }
        } catch (Exception e) {
            int x = this.getSnake().getPositions().getFirst().getX();
            int y = this.getSnake().getPositions().getFirst().getY();
            int sizeX = this.getMap().getSizeX();
            int sizeY = this.getMap().getSizeY();

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
        }

        return null;
    }
}
