package tp1progreseau.gameElement;

import tp1progreseau.gameElement.fabrique.*;
import java.util.ArrayList;
import tp1progreseau.model.*;
import tp1progreseau.movement.*;
import tp1progreseau.utils.*;



public class Snake extends GameElement {
	
	private ArrayList<Position> initialPositions;
	private ArrayList<Position> positions;
	private AgentAction action;
	private int id;
	private Movement movement;
	private boolean dead;
	private int taille;
	private int stateSick;
    private int stateInvisible;


	public Snake(int id,Features informations,ArrayList<Position> positions,AgentAction action,TypeSnake type)
    {
		super(informations,positions.get(positions.size()-1));
		this.initialPositions = positions;
		this.positions = positions;
		this.action = action;
		this.id  = id;
		this.dead = false;
		this.taille = 1;
		this.stateSick = 0;
        this.stateInvisible = 0;
		
		switch (type) {
			
			case TypeSnake.RANDOM -> this.movement = new RandomMovement(this);
			
			case TypeSnake.HUMAN -> this.movement = new HumanMovement(this);


			case TypeSnake.IA -> this.movement = new AiMovement(this);
		}
    } 

    
    public ArrayList<Position> getPositions() {
		return this.positions;
	}

	@Override
	public Position getPosition() {
		return this.positions.getFirst();
	}

	public int getTaille()
	{
		return this.taille;
	}

	public void moveAgent(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map) {
		AgentAction localAction = this.action;
	
		if (!SnakeGame.isLegalMove(this, localAction)) {
			localAction = this.getLasAction();
		}
	
		Position p = new Position(this.positions.getFirst().getX(), this.positions.getFirst().getY());
	
		if (AgentAction.MOVE_DOWN == localAction) {
			if (map.getSizeY() <= p.getY() + 1) {
				p.setY(0);
			} else {
				p.setY(p.getY() + 1);
			}
		}
	
		if (AgentAction.MOVE_LEFT == localAction) {
			if (0 > p.getX() - 1) {
				p.setX(map.getSizeX() - 1);
			} else {
				p.setX(p.getX() - 1);
			}
		}
	
		if (AgentAction.MOVE_RIGHT == localAction) {
			if (map.getSizeX() <= p.getX() + 1) {
				p.setX(0);
			} else {
				p.setX(p.getX() + 1);
			}
		}
	
		if (AgentAction.MOVE_UP == localAction) {
			if (0 > p.getY() - 1) {
				p.setY(map.getSizeY() - 1);
			} else {
				p.setY(p.getY() - 1);
			}
		}
		this.uptadeEtatSnake();
		this.isDead(map);
		this.action = localAction;
		this.resize(p);
	}
	
	public FeaturesSnake MakeFeaturesSnake()
	{
		FeaturesSnake featuresSnake = (FeaturesSnake) super.getInformations();
		featuresSnake.setInvincible(this.isInvisible());
		featuresSnake.setSick(this.isSick());
		featuresSnake.setPositions(this.positions);
		featuresSnake.setLastAction(this.action);
		return featuresSnake;
	}

	public void growth()
	{
		this.taille +=1;
	}


	public AgentAction getLasAction()
	{
		FeaturesSnake featuresSnake = (FeaturesSnake) super.getInformations();
		return featuresSnake.getLastAction();
	}

	public AgentAction getAction()
	{
		return this.action;
	}

	public AgentAction genMove(ArrayList<FeaturesSnake> snakes, ArrayList<FeaturesItem> items, InputMap map)
	{
		return this.movement.apply(snakes,items,map);
	}

	public void resize(Position p)
	{
		this.positions.addFirst(p);
		if (this.positions.size() > this.taille) this.positions.removeLast();

	}

	public boolean isDead(InputMap map){
		
		if (!this.dead && !this.isInvisible()){

			boolean[][] walls = map.get_walls();
			
			if(walls[this.getPosition().getX()][this.getPosition().getY()]) 
			{
				this.dead = true;
			}

			for (int i = 0; i < this.positions.size(); i++) 
			{
				Position position = this.positions.get(i);
				
				for (int j = 0; j < this.positions.size(); j++) 
				{
					if (position.getX() == this.positions.get(j).getX() &&  position.getY() == this.positions.get(j).getY() && j != i ) 
					{
						this.dead = true;
					}
				}
				

				for (int j = 0; j < map.getStart_snakes().size(); j++) 
				{
					if (this.id != j )
					{
						FeaturesSnake OtherSnake = map.getStart_snakes().get(j);

						for(Position otherPosition : OtherSnake.getPositions())
						{
							if (position.equals(otherPosition)) 
							{
								this.dead = true;
							}
						}
					}
				}
			}

		}
		return this.dead;
	}

	public int getId()
	{
		return this.id;
	}

	public void resetToInitialPosition() {
		if (!this.initialPositions.isEmpty()) {
			this.positions.clear();
			this.positions.addAll(this.initialPositions); 
		}
	}
	


	public void setMethodMove(TypeSnake type)
	{
		switch (type) {
			
			case TypeSnake.RANDOM:
				this.movement = new RandomMovement(this);
			
			case TypeSnake.HUMAN:
				this.movement = new HumanMovement(this);
			
			case TypeSnake.IA:
				this.movement = new AiMovement(this);

		}
	}

	public boolean isSick()
    {
        return this.stateSick > 0;
    }

    public boolean isInvisible()
    {
        return this.stateInvisible > 0;
    }

	public void becomeSick()
    {
        this.stateSick = 20;
    }

    
    public void becomeInvisible()
    {
        this.stateInvisible = 20;
    }

	public void uptadeEtatSnake()
	{
		if (this.isInvisible()) this.stateInvisible--;
		if(this.isSick()) this.stateSick--;
	}
	
	public void setMovement(AgentAction movement)
	{
		this.action = movement ;
	}
}
