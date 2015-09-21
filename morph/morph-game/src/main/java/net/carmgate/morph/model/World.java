package net.carmgate.morph.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;

import net.carmgate.morph.GameLoaded;
import net.carmgate.morph.events.WorldEvent;
import net.carmgate.morph.events.WorldEventFactory;
import net.carmgate.morph.events.WorldEventType;
import net.carmgate.morph.events.entities.ship.ShipAdded;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MEvent;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.animations.world.WorldAnimation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.components.ComponentFactory;
import net.carmgate.morph.model.entities.ship.Ship;

@Singleton
public class World {

	public static enum TimeFreezeCause {
		PAUSE_ACTION,
		COMPONENT_DRAG,
		SHIP_EDITOR;
	}

	public static String readFile(String path, Charset encoding)
			throws IOException {
		final byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Inject private Logger LOGGER;
	@Inject private MEvent<WorldEvent> worldEventMgr;
	@Inject private Event<GameLoaded> gameLoadedEvent;
	// @Inject private OrderFactory orderFactory;
	@Inject private PhysicalEntityFactory entityFactory;
	@Inject private WorldEventFactory worldEventFactory;
	@Inject private MEventManager eventManager;
	@Inject private ComponentFactory componentFactory;

	private final List<Ship> ships = new ArrayList<>();
	private final List<PhysicalEntity> nonShipsPhysicalEntities = new ArrayList<>();
	private final Set<PhysicalEntity> physicalEntities = new HashSet<>();
	private final Map<String, Player> players = new HashMap<>();
	private final List<WorldAnimation> animations = new ArrayList<>();

	private long lastUpdateTime = 0;
	private long time = 0;
	private long absoluteTime = 0;
	private float timeFactor = 1f;
	private boolean timeFrozen = false;
	private Ship playerShip;
	private TimeFreezeCause timeFreezeCause;

	public void add(PhysicalEntity entity) {
		if (entity instanceof Ship) {
			add((Ship) entity);
			return;
		}

		nonShipsPhysicalEntities.add(entity);
		physicalEntities.add(entity);
	}
	public void add(Player player) {
		players.put(player.getName(), player);
	}
	private void add(Ship ship) {
		// TODO modify this so that ships have limited line of sight
		// Fill the ship
		ship.getPlayer().add(ship);

		// Update world
		ships.add(ship);
		physicalEntities.add(ship);

		// update surroundings of the awares
		final ShipAdded shipAdded = worldEventFactory.newInstance(WorldEventType.SHIP_ADDED);
		shipAdded.setAddedShip(ship);
		worldEventMgr.fire(shipAdded);
	}
	public long getAbsoluteTime() {
		return absoluteTime;
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
	@SuppressWarnings("unused")
	private void init(@Observes ContainerInitialized containerInitializedEvent) {
		eventManager.scanAndRegister(this);

		new Thread((Runnable) () -> {
			final ScriptEngineManager manager = new ScriptEngineManager();
			final ScriptEngine engine = manager.getEngineByName("nashorn"); //$NON-NLS-1$
			try {
				InputStream in = getClass().getResourceAsStream("/model-init.js");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				engine.put("world", World.this);
				// engine.put("orderFactory", orderFactory);
				engine.put("entityFactory", entityFactory);
				engine.put("componentFactory", componentFactory);
				engine.eval(reader);
				gameLoadedEvent.fire(new GameLoaded());
			} catch (final Exception e) {
				LOGGER.error("Cannot open init file", e); //$NON-NLS-1$
			}

			for (Ship ship : getShips()) {
				if ("Me".equals(ship.getPlayer().getName())) {
					playerShip = ship;
				}
			}
		}, "model init").start(); //$NON-NLS-1$

	}

	public boolean isTimeFrozen() {
		return timeFrozen;
	}

	@SuppressWarnings("unused")
	private void onShipDeath(@MObserves ShipDeath shipDeath) {
		remove(shipDeath.getShip());
	}

	private void remove(Ship ship) {
		ships.remove(ship);
		physicalEntities.remove(ship);
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
