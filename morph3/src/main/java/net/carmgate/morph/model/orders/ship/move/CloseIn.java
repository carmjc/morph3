package net.carmgate.morph.model.orders.ship.move;

import javax.inject.Inject;

import net.carmgate.morph.eventmgt.MEvent;
import net.carmgate.morph.eventmgt.MObserves;
import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.events.ShipDeath;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.orders.OrderFactory;
import net.carmgate.morph.model.orders.OrderType;
import net.carmgate.morph.model.physics.ForceSource;

public class CloseIn extends MoveOrder implements ForceSource {

	@Inject private MEvent<Order> orderMgr;
	@Inject private OrderFactory orderFactory;

	private Ship target;
	private float distance;
	private final Vector2f force = new Vector2f();
	private final Vector2f tmpVect = new Vector2f();

	@Override
	protected void evaluate() {
		tmpVect.copy(target.getPos()).sub(getOrderee().getPos());
		if (tmpVect.length() > distance) {
			force.copy(target.getPos()).sub(getOrderee().getPos()).sub(getOrderee().getSpeed());
			force.scale(Ship.MAX_PROPULSOR_FORCE / force.length());
		}
	}

	public float getDistance() {
		return distance;
	}

	@Override
	public Vector2f getForce() {
		return force;
	}

	public Ship getTarget() {
		return target;
	}

	protected void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (target == shipDeath.getShip()) {
			final NoMoveOrder noMoveOrder = orderFactory.newInstance(OrderType.NO_MOVE);
			orderMgr.fire(noMoveOrder);
		}
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public void setTarget(Ship target) {
		this.target = target;
	}

}
