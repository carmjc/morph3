package net.carmgate.morph.model.entities.physical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.events.ShipDead;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.WorldEvent;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.api.Renderable;

import org.slf4j.Logger;

public class Ship implements Renderable, PhysicalEntity {

	@Inject
	private World world;
	@Inject
	private Event<WorldEvent> worldEventMgr;
	@Inject
	private Logger LOGGER;

	private final Vector2f pos = new Vector2f();
	private final Vector2f speed = new Vector2f();
	private float mass;
	private Player owner;
	private final Surroundings surroundings = new Surroundings();
	private final Set<ForceSource> forceSources = new HashSet<>();
	private final List<Order> orders = new ArrayList<>();
	private float health;

	public void add(Order order) {
		order.setWorld(world);

		orders.add(order);

		if (order instanceof ForceSource) {
			forceSources.add((ForceSource) order);
		}
	}

	public Order getCurrentOrder() {
		if (orders.size() > 0) {
			return orders.get(0);
		}
		return null;
	}

	@Override
	public Set<ForceSource> getForceSources() {
		return forceSources;
	}

	public float getHealth() {
		return health;
	}

	@Override
	public float getMass() {
		return mass;
	}

	public Player getPlayer() {
		return owner;
	}

	@Override
	public Vector2f getPos() {
		return pos;
	}

	@Override
	public Vector2f getSpeed() {
		return speed;
	}

	public Surroundings getSurroundings() {
		return surroundings;
	}

	public void init(Vector2f pos, float health) {
		this.health = health;
		this.pos.copy(pos);
	}

	public void onShipHit(@Observes ShipHit event) {
		if (event.getShip() == this) {
			health -= event.getDamage();
			LOGGER.debug("Ship hit. Health: " + health);
			if (health <= 0) {
				worldEventMgr.fire(new ShipDead(this));
			}
		} else {
			LOGGER.debug("Ship hit. Not this ship");
		}
	}

	// FIXME
	//	@Override
	//	public void onWorldUpdate(@Observes WorldEvent event) {
	//		if (event instanceof ShipAdded) {
	//			final Ship ship = ((ShipAdded) event).getShip();
	//			if (ship == this) {
	//				// add the other ships
	//				// TODO we should do this otherwise
	//				for (final Ship tmpShip : world.getShips()) {
	//					if (tmpShip != this) {
	//						surroundings.addShip(tmpShip);
	//					}
	//				}
	//			} else {
	//				// add the ship
	//				surroundings.addShip(ship);
	//			}
	//		}
	//		if (event instanceof ShipHit) {
	//			final ShipHit shipHit = (ShipHit) event;
	//			if (shipHit.getShip() == this) {
	//				health -= shipHit.getDamage();
	//				if (health <= 0) {
	//					worldEventMgr.fire(new ShipDead(this));
	//				}
	//			}
	//
	//		}
	//	}
	//
	public void setMass(float mass) {
		this.mass = mass;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

}
