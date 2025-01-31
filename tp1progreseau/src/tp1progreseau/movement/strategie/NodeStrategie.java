package tp1progreseau.movement.strategie;

import java.util.*;

import tp1progreseau.utils.AgentAction;

public class NodeStrategie {

    private static int nbCouches;
    private final int x;
    private final int y;
    private final NodeStrategie parentNode;
    private final List<NodeStrategie> childNodes;
    private final AgentAction mouvement;
    private final boolean endWay;
    private int cost; 

    private NodeStrategie(int x, int y, AgentAction mouvement) {
        this.x = x;
        this.y = y;
        this.mouvement = mouvement;
        this.parentNode = null;
        this.childNodes = new LinkedList<>();
        this.endWay = false;
        this.cost = 0; 
    }

    private NodeStrategie(int x, int y, AgentAction mouvement, NodeStrategie parentNode, boolean isEndWay) {
        this.x = x;
        this.y = y;
        this.mouvement = mouvement;
        this.parentNode = parentNode;
        this.childNodes = new LinkedList<>();
        this.endWay = isEndWay;
        this.cost = parentNode.getCost() + 1; 
    }

    public NodeStrategie getParentNode() {
        return parentNode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<NodeStrategie> getChildNodes() {
        return childNodes;
    }

    public AgentAction getMouvement() {
        return mouvement;
    }

    public static int getNbCouches() {
        return nbCouches;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public static NodeStrategie buildRoot(int x, int y, AgentAction mouvement) {
        return new NodeStrategie(x, y, mouvement);
    }

    private NodeStrategie buildChild(int newX, int newY, AgentAction action, ModeStrategie strategie) {
        boolean isEndWay = strategie.isDangerous(newX, newY);
        return new NodeStrategie(newX, newY, action, this, isEndWay );
    }

    public void buildChildNodes(ModeStrategie strategie) {
        if (this.endWay) return;

        int sizeX = strategie.getSizeX();
        int sizeY = strategie.getSizeY();

        if(strategie.getSnake().getTaille() != 1) {
            if (mouvement != AgentAction.MOVE_LEFT) {
                childNodes.add(buildChild((x + 1) % sizeX, y, AgentAction.MOVE_RIGHT, strategie));
            }
            if (mouvement != AgentAction.MOVE_RIGHT) {
                childNodes.add(buildChild((x - 1 + sizeX) % sizeX, y, AgentAction.MOVE_LEFT, strategie));
            }
            if (mouvement != AgentAction.MOVE_DOWN) {
                childNodes.add(buildChild(x, (y - 1 + sizeY) % sizeY, AgentAction.MOVE_UP, strategie));
            }
            if (mouvement != AgentAction.MOVE_UP) {
                childNodes.add(buildChild(x, (y + 1) % sizeY, AgentAction.MOVE_DOWN, strategie));
            }
        } else {
            childNodes.add(buildChild((x + 1) % sizeX, y, AgentAction.MOVE_RIGHT, strategie));
            childNodes.add(buildChild((x - 1 + sizeX) % sizeX, y, AgentAction.MOVE_LEFT, strategie));
            childNodes.add(buildChild(x, (y - 1 + sizeY) % sizeY, AgentAction.MOVE_UP, strategie));
            childNodes.add(buildChild(x, (y + 1) % sizeY, AgentAction.MOVE_DOWN, strategie));
        }
    }

    public NodeStrategie breadthFirstSearch(ModeStrategie strategie, int targetX, int targetY) {
        Queue<NodeStrategie> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        NodeStrategie rootNode = NodeStrategie.buildRoot(
            strategie.getSnake().getPositions().getFirst().getX(),
            strategie.getSnake().getPositions().getFirst().getY(),
            null
        );
        queue.add(rootNode);
        visited.add(rootNode.getX() + "," + rootNode.getY());

        while (!queue.isEmpty()) {
            NodeStrategie currentNode = queue.poll();

            if (currentNode.getX() == targetX && currentNode.getY() == targetY) {
                return currentNode;
            }

            currentNode.buildChildNodes(strategie);
            for (NodeStrategie child : currentNode.getChildNodes()) {
                String childCoord = child.getX() + "," + child.getY();

                if (!visited.contains(childCoord)) {
                    queue.add(child);
                    visited.add(childCoord);
                }
            }
        }

        return null; 
    }


    

    
}
