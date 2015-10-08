package net.carmgate.morph.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.ship.Ship;

@Singleton
@Entity
public class MWorld {

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
	@Transient private final Map<Class<? extends Animation>, List<Animation>> animations = new HashMap<>();

	private long lastUpdateTime = 0;
	private long time = 0;
	private long absoluteTime = 0;
	private float timeFactor = 1f;
	private boolean timeFrozen = false;
	@OneToOne(cascade = CascadeType.ALL) private Ship playerShip;
	private TimeFreezeCause timeFreezeCause;
	private long aiLastUpdate;
	@Transient private long millisSinceLastUpdate;
	@Transient private World box2dWorld;
	private float worldMillisSinceLastUpdate;
	private float worldMillisSinceLastBox2dUpdate;

	public void add(Player player) {
		players.put(player.getName(), player);
	}

	public void addAnimation(Animation anim) {
		List<Animation> animList = animations.get(anim.getClass());
		if (animList == null) {
			animList = new ArrayList<>();
			animations.put(anim.getClass(), animList);
		}
		animList.add(anim);
	}

	public long getAbsoluteTime() {
		return absoluteTime;
	}

	public long getAiLastUpdate() {
		return aiLastUpdate;
	}

	public Map<Class<? extends Animation>, List<Animation>> getAnimations() {
		return animations;
	}

	public World getBox2dWorld() {
		return box2dWorld;
	}

	public long getMillisSinceLastUpdate() {
		return millisSinceLastUpdate;
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

	public float getWorldMillisSinceLastBox2dUpdate() {
		return worldMillisSinceLastBox2dUpdate;
	}

	public float getWorldMillisSinceLastUpdate() {
		return worldMillisSinceLastUpdate;
	}

	// @PostConstruct
	public boolean isTimeFrozen() {
		return timeFrozen;
	}

	public void loadFrom(MWorld newWorld) {
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

	@PostConstruct private void postConstruct() {
		box2dWorld = new World(new Vec2(0, 0));
	}

	@PostLoad private void postLoad() {
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

	public void removeAnimation(Animation anim) {
		List<Animation> animList = animations.get(anim.getClass());
		animList.remove(anim);
	}

	public void resetLastUpdateTime() {
		lastUpdateTime = new Date().getTime();
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

	public void setWorldMillisSinceLastBox2dUpdate(float worldMillisSinceLastBox2dUpdate) {
		this.worldMillisSinceLastBox2dUpdate = worldMillisSinceLastBox2dUpdate;
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
			millisSinceLastUpdate = newUpdateTime - lastUpdateTime;

			worldMillisSinceLastUpdate = millisSinceLastUpdate * timeFactor;
			worldMillisSinceLastBox2dUpdate += millisSinceLastUpdate * timeFactor;

			time += worldMillisSinceLastUpdate;
			absoluteTime += millisSinceLastUpdate;
		}

		lastUpdateTime = newUpdateTime;
	}

}
