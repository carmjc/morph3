package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import net.carmgate.morph.model.entities.Ship;

import org.lwjgl.util.vector.Vector2f;

@Singleton
public class Model {

	private final List<Ship> ships = new ArrayList<>();

	public void addShip(Ship ship) {
		ships.add(ship);
	}

	public List<Ship> getShips() {
		return ships;
	}

	@PostConstruct
	private void init() {
		final Ship firstShip = new Ship(new Vector2f(120f, 250f));
		ships.add(firstShip);
	}

	public void removeShip(Ship ship) {
		ships.remove(ship);
	}
}
