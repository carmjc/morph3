package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import net.carmgate.morph.model.entities.ship.Ship;

@Entity
public class Player {
	public static enum PlayerType {
		AI, PLAYER;
	}

	private static int newId = 0;

	@Id private int id = newId++;
	private String name;
	@OneToMany(mappedBy = "owner")
	private List<Ship> ships = new ArrayList<>();
	private float[] color;
	private PlayerType playerType;

	public Player() {
	}

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

	public int getId() {
		return id;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
}
