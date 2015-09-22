package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.animations.world.WorldAnimation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;

@Singleton
@Entity
public class World {

	public static enum TimeFreezeCause {
		PAUSE_ACTION,
		COMPONENT_DRAG,
		SHIP_EDITOR;
	}

	@Id private int id = 1;
	@Transient @OneToMany(cascade = CascadeType.ALL) private final List<Ship> ships = new ArrayList<>();
	@Transient @OneToMany(cascade = CascadeType.ALL) private final List<PhysicalEntity> nonShipsPhysicalEntities = new ArrayList<>();
	@OneToMany(cascade = CascadeType.ALL) private final Set<PhysicalEntity> physicalEntities = new HashSet<>();
	@OneToMany(cascade = CascadeType.ALL) private final Map<String, Player> players = new HashMap<>();
	@Transient private final List<WorldAnimation> animations = new ArrayList<>();

	private long lastUpdateTime = 0;
	private long time = 0;
	private long absoluteTime = 0;
	private float timeFactor = 1f;
	private boolean timeFrozen = false;
	@OneToOne(cascade = CascadeType.ALL) private Ship playerShip;
	private TimeFreezeCause timeFreezeCause;
	private long aiLastUpdate;

	public void add(Player player) {
		players.put(player.getName(), player);
	}

	public long getAbsoluteTime() {
		return absoluteTime;
	}
	public long getAiLastUpdate() {
		return aiLastUpdate;
	}

	public List<PhysicalEntity> getNonShipsPhysicalEntities() {
		return nonShipsPhysicalEntities;
	}

	public Set<PhysicalEntity> getPhysicalEntities() {
		return physicalEntities;
	}

	public Map<String, Player> getPlayers() {
		return players;
	}

	public Ship getPlayerShip() {
		return playerShip;
	}

	/**
	 * Do not use this method if you intend to modify this list.
	 */
	public List<Ship> getShips() {
		return ships;
	}

	public long getTime() {
		return time;
	}

	public TimeFreezeCause getTimeFreezeCause() {
		return timeFreezeCause;
	}

	public List<WorldAnimation> getWorldAnimations() {
		return animations;
	}

	// @PostConstruct
	public boolean isTimeFrozen() {
		return timeFrozen;
	}

	public void loadFrom(World newWorld) {
		// change all lists
		ships.clear();
		ships.addAll(newWorld.getShips());

		nonShipsPhysicalEntities.clear();
		nonShipsPhysicalEntities.addAll(newWorld.getNonShipsPhysicalEntities());

		physicalEntities.clear();
		physicalEntities.addAll(newWorld.getPhysicalEntities());

		players.clear();
		players.putAll(newWorld.getPlayers());
	}

	@SuppressWarnings("unused")
	private void onShipDeath(@MObserves ShipDeath shipDeath) {
		remove(shipDeath.getShip());
	}

	@PostLoad
	private void postLoad() {
		for (PhysicalEntity entity : getPhysicalEntities()) {
			if (entity instanceof Ship) {
				getShips().add((Ship) entity);
			} else {
				getNonShipsPhysicalEntities().add(entity);
			}
		}
	}

	private void remove(Ship ship) {
		ships.remove(ship);
		physicalEntities.remove(ship);
	}

	public void setAiLastUpdate(long aiLastUpdate) {
		this.aiLastUpdate = aiLastUpdate;
	}

	public void setPlayerShip(Ship playerShip) {
		this.playerShip = playerShip;
	}

	public void setTimeFactor(int timeFactor) {
		this.timeFactor = timeFactor;
	}

	public void toggleTimeFrozen(TimeFreezeCause timeFreezeCause) {
		if (!timeFrozen) {
			this.timeFreezeCause = timeFreezeCause;
		} else {
			this.timeFreezeCause = null;
		}
		timeFrozen = !timeFrozen;
	}

	public void updateTime() {
		long newUpdateTime = new Date().getTime();
		if (lastUpdateTime != 0) {
			float timeFactor = timeFrozen ? 0 : this.timeFactor;
			time += (newUpdateTime - lastUpdateTime) * timeFactor;
			absoluteTime += newUpdateTime - lastUpdateTime;
		}
		lastUpdateTime = newUpdateTime;
	}

}
