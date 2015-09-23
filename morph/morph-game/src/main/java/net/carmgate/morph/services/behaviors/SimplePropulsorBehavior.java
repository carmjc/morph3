package net.carmgate.morph.services.behaviors;

import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;

public class SimplePropulsorBehavior extends ComponentBehavior<SimplePropulsor> {

	@Inject private Conf conf;

	private Vector2f tmpVect = new Vector2f();

	private void endBehavior(SimplePropulsor cmp) {
		cmp.setActive(false);
		cmp.setTarget(null);
	}

	@Override
	public void eval(SimplePropulsor cmp) {

		if (cmp.getTargetPosInWorld() == null) {
			cmp.setActive(false);
			return;
		}

		// target offset
		tmpVect.copy(cmp.getTargetPosInWorld()).sub(cmp.getShip().getPos());
		float actualDistance = tmpVect.length();

		float epsilon = conf.getFloatProperty("order.moveOrder.epsilon"); //$NON-NLS-1$
		if (actualDistance < epsilon && cmp.getShip().getSpeed().lengthSquared() < epsilon) {
			if (!cmp.getShip().isForceStop()) {
				cmp.getShip().setForceStop(true);
				cmp.getForce().copy(Vector2f.NULL);
			}
			endBehavior(cmp);
			return;
		}

		float maxAccel = Ship.MAX_PROPULSOR_FORCE / cmp.getShip().getMass();

		if (actualDistance < 0) {
			cmp.getForce().copy(cmp.getShip().getSpeed()).scale(-1);
		} else {
			cmp.getForce().copy(cmp.getTargetPosInWorld()).sub(cmp.getShip().getPos());
			tmpVect.copy(cmp.getShip().getSpeed()).scale(cmp.getShip().getSpeed().length() / maxAccel);
			cmp.getForce().sub(tmpVect); // .add(target.getSpeed())
			if (cmp.getShip().getSpeed().lengthSquared() > 0) {
				cmp.getForce().scale(cmp.getShip().getSpeed().lengthSquared() / (2 * actualDistance + cmp.getShip().getSpeed().length()));
			}
		}

		float length = cmp.getForce().length();
		if (length > Ship.MAX_PROPULSOR_FORCE) {
			cmp.getForce().scale(Ship.MAX_PROPULSOR_FORCE / length);
		}

		// set orientation
		// TODO This is a very basic orientating method
		if (cmp.getForce() != null && cmp.getForce().length() != 0) {
			float angle = (float) (cmp.getForce().angleWith(Vector2f.J) / Math.PI * 180);
			if (cmp.getForce().x * cmp.getShip().getSpeed().x + cmp.getForce().y * cmp.getShip().getSpeed().y < 0) {
				angle += 180;
			}
			cmp.getShip().setRotationTarget(angle);
		}
	}

	@Override
	public final void init(SimplePropulsor cmp) {
		cmp.setActive(true);

		// // Apply force to ship
		cmp.getShip().getForceSources().add(cmp);
	}

}
