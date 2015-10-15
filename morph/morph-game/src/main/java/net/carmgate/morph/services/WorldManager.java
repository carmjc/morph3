package net.carmgate.morph.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jbox2d.dynamics.Body;
import org.slf4j.Logger;

import net.carmgate.morph.GameLoaded;
import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEvent;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.WorldEvent;
import net.carmgate.morph.events.world.WorldEventFactory;
import net.carmgate.morph.events.world.entities.ship.ShipAdded;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.ComponentAnimation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.components.ComponentFactory;
import net.carmgate.morph.model.entities.ship.Ship;

public class WorldManager {

	public static enum InitMethod {
		JS, DB;
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
	@Inject private ShipManager shipManager;
	@Inject private ComponentManager componentManager;
	@Inject private MWorld world;
	@Inject private Conf conf;
	@Inject private EntityManager entityManager;

	public void add(PhysicalEntity entity) {
		Body body = world.getBox2dWorld().createBody(entity.getBodyDef());
		entity.setBody(body);

		if (entity instanceof Ship) {
			add((Ship) entity);
			body.createFixture(entity.getFixtureDef(entity.getShape()));
			body.setUserData(entity);
		} else {
			world.getNonShipsPhysicalEntities().add(entity);
			world.getPhysicalEntities().add(entity);
		}
	}

	private void add(Ship ship) {
		// TODO modify this so that ships have limited line of sight
		// Fill the ship
		ship.getPlayer().add(ship);

		// Update world
		world.getShips().add(ship);
		world.getPhysicalEntities().add(ship);

		// update surroundings of the awares
		final ShipAdded shipAdded = worldEventFactory.newInstance(ShipAdded.class);
		shipAdded.setAddedShip(ship);
		worldEventMgr.fire(shipAdded);
	}

	@PostConstruct
	private void init() {
		eventManager.scanAndRegister(this);
	}

	@SuppressWarnings("unused")
	private void init(@Observes ContainerInitialized containerInitializedEvent) {
		eventManager.scanAndRegister(this);

		new Thread((Runnable) () -> {
			{
				if (InitMethod.JS.name().equals(conf.getProperty("initWorldMethod"))) {
					initWithJsScript();
				} else if (InitMethod.DB.name().equals(conf.getProperty("initWorldMethod"))) {
					initWithDb();
				}
			}
		} , "model init").start(); //$NON-NLS-1$

	}

	private void initWithDb() {
		MWorld newWorld = entityManager.find(MWorld.class, 1);
		world.loadFrom(newWorld);
		gameLoadedEvent.fire(new GameLoaded());

		for (Ship ship : world.getShips()) {
			if ("Me".equals(ship.getPlayer().getName())) {
				world.setPlayerShip(ship);
			}
			ship.getBody().getLinearVelocity().set(0, 0);
		}

	}

	private void initWithJsScript() {
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByName("nashorn"); //$NON-NLS-1$
		try {
			InputStream in = getClass().getResourceAsStream("/model-init.js");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			engine.put("world", world);
			// engine.put("orderFactory", orderFactory);
			engine.put("entityFactory", entityFactory);
			engine.put("componentFactory", componentFactory);
			engine.put("shipManager", shipManager);
			engine.put("componentManager", componentManager);
			engine.put("worldManager", this);
			engine.eval(reader);

			for (Ship ship : world.getShips()) {
				if ("Me".equals(ship.getPlayer().getName())) {
					world.setPlayerShip(ship);
				}
			}

			gameLoadedEvent.fire(new GameLoaded());
		} catch (final Exception e) {
			LOGGER.error("Cannot open init file", e); //$NON-NLS-1$
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private void onShipDeath(final @MObserves ShipDeath shipDeath) {
		world.getAnimations().values().forEach(list -> {
			List<ComponentAnimation> toDelete = new ArrayList<>();

			list.forEach(anim -> {
				if (anim instanceof ComponentAnimation) {
					Ship ship = ((ComponentAnimation) anim).getSource().getShip();
					if (ship == shipDeath.getShip()) {
						toDelete.add((ComponentAnimation) anim);
					}
				}
			});

			list.removeAll(toDelete);
		});

		world.getBox2dWorld().destroyBody(shipDeath.getShip().getBody());
		world.remove(shipDeath.getShip());
	}

}
