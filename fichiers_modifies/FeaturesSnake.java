package tp1progreseau.utils;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeaturesSnake extends Features {


	@JsonProperty("positions")
	ArrayList<Position> positions;
	
	@JsonProperty("lastAction")
	private AgentAction lastAction;
	
	@JsonProperty("colorSnake")
	ColorSnake colorSnake;
	
	@JsonProperty("invincible")
	boolean isInvincible;
	@JsonProperty("sick")
	boolean isSick;
	
	public FeaturesSnake()
	{
		
	}
	
	public FeaturesSnake(ArrayList<Position> positions, AgentAction lastAction, ColorSnake colorSnake, boolean isInvincible, boolean isSick) {
		
		this.positions = positions;
		this.colorSnake = colorSnake;
		this.lastAction = lastAction;
		
		this.isInvincible = isInvincible;
		
		this.isSick = isSick;
		
	}
		
	
	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}




	public ColorSnake getColorSnake() {
		return colorSnake;
	}


	public void setColorSnake(ColorSnake colorSnake) {
		this.colorSnake = colorSnake;
	}


	public boolean isInvincible() {
		return isInvincible;
	}


	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}


	public boolean isSick() {
		return isSick;
	}


	public void setSick(boolean isSick) {
		this.isSick = isSick;
	}


	public AgentAction getLastAction() {
		return lastAction;
	}


	public void setLastAction(AgentAction lastAction) {
		this.lastAction = lastAction;
	}

}
