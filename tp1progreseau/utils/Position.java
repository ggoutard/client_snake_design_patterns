package tp1progreseau.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {

	@JsonProperty("x")
	private int x;
	@JsonProperty("y")
	private int y;

	public Position() {}

	public Position(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}