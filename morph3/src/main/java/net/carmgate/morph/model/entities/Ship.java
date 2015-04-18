package net.carmgate.morph.model.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.events.ShipAdded;
import net.carmgate.morph.model.events.ShipDead;
import net.carmgate.morph.model.events.ShipHit;
import net.carmgate.morph.model.events.ShipUpdated;
import net.carmgate.morph.model.events.WorldUpdateListener;
import net.carmgate.morph.model.events.WorldUpdated;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;
import net.carmgate.morph.ui.renderers.api.Renderable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ship implements Renderable, WorldUpdateListener, PhysicalEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ship.class);

	private final Vector2f pos = new Vector2f();
	private final Vector2f speed = new Vector2f();
	private float mass;
	private Player owner;
	private final Surroundings surroundings = new Surroundings();
	private final Set<ForceSource> forceSources = new HashSet<>();
	private final List<Order> orders = new ArrayList<>();
	private World world;
	private float health;
	private final Set<ShipUpdatedListener> shipUpdatedListeners = new HashSet<>();

	public Ship(Vector2f pos, float health) {
		this.health = health;
		this.pos.copy(pos);
	}

	public void add(Order order) {
		order.setWorld(world);

		orders.add(order);

		if (order instanceof ForceSource) {
			forceSources.add((ForceSource) order);
		}
	}

	public void addShipUpdatedListener(ShipUpdatedListener listener) {
		shipUpdatedListeners.add(listener);
	}

	public void fireShipUpdate(ShipUpdated event) {
		shipUpdatedListeners.forEach(listener -> {
			listener.onShipUpdated(event);
		});
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

	public World getWorld() {
		return world;
	}

	@Override
	public void onWorldUpdate(WorldUpdated event) {
		if (event instanceof ShipAdded) {
			final Ship ship = ((ShipAdded) event).getShip();
			if (ship != this) {
				// add the ship
				surroundings.addShip(ship);
			} else {
				// add the other ships
				// TODO we should do this otherwise
				for (final Ship tmpShip : ((ShipAdded) event).getWorld().getShips()) {
					if (tmpShip != this) {
						surroundings.addShip(tmpShip);
					}
				}
			}
		}
		if (event instanceof ShipHit) {
			final ShipHit shipHit = (ShipHit) event;
			if (shipHit.getShip() == this) {
				health -= shipHit.getDamage();
				if (health <= 0) {
					fireShipUpdate(new ShipDead(this));
				}
			}

		}
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public void setWorld(World world) {
		this.world = world;
	}

}
