package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.List;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class Player {
	public static enum PlayerType {
		AI, PLAYER;
	}

	private final String name;
	private List<Ship> ships = new ArrayList<>();
	private float[] color;
	private PlayerType playerType;

	public Player(String name, PlayerType playerType) {
		this.name = name;
		this.playerType = playerType;
	}

	public void add(Ship ship) {
		ships.add(ship);
	}

	public float[] getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public List<Ship> getShips() {
		return ships;
	}

	public void setColor(float[] color) {
		this.color = color;
	}
}
