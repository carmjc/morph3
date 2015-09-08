package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;

@NeedsTarget
@ComponentKind(ComponentType.PROPULSORS)
public class SimplePropulsor extends Component implements ForceSource {

	@Inject private World world;
	@Inject private Logger LOGGER;

	@Inject private Conf conf;

	private float desiredDistance = 100;
	private final Vector2f force = new Vector2f();

	private final Vector2f tmpVect = new Vector2f();

	private void endBehavior() {
		setActive(false);
		setTarget(null);
	}

	@Override
	public void evalBehavior() {
		if (getTargetPosInWorld() == null) {
			setActive(false);
			return;
		}

		// LOGGER.debug("Closing in on (" + getTargetPosInWorld() + ") from (" + getShip().getPos() + ")");

		// target offset
		tmpVect.copy(getTargetPosInWorld()).sub(getShip().getPos());
		float actualDistance = tmpVect.length() - desiredDistance;

		float epsilon = conf.getFloatProperty("order.moveOrder.epsilon"); //$NON-NLS-1$
		if (actualDistance < epsilon && getShip().getSpeed().lengthSquared() < epsilon) {
			if (!getShip().isForceStop()) {
				getShip().setForceStop(true);
				force.copy(Vector2f.NULL);
			}
			endBehavior();
			return;
		}

		float maxAccel = Ship.MAX_PROPULSOR_FORCE / getShip().getMass() * getShip().getComponentsComposition().get(ComponentType.PROPULSORS);

		if (actualDistance < 0) {
			force.copy(getShip().getSpeed()).scale(-1);
			// getShip().debug1.copy(Vector2f.NULL);
			// getShip().debug2.copy(Vector2f.NULL);
			// getShip().debug3.copy(Vector2f.NULL);
			// getShip().debug4.copy(Vector2f.NULL);
		} else {
			// LOGGER.debug("dist: " + actualDistance);
			force.copy(getTargetPosInWorld()).sub(getShip().getPos());
			// getShip().debug1.copy(force);
			// LOGGER.debug("toTarget: " + force.length());
			tmpVect.copy(getShip().getSpeed()).scale(getShip().getSpeed().length() / maxAccel);
			// getShip().debug2.copy(tmpVect);
			// LOGGER.debug("adjusted speed: " + tmpVect.length());
			force.sub(tmpVect); // .add(target.getSpeed())
			// getShip().debug3.copy(force);
			// LOGGER.debug("desiredSpeed: " + force.length());
			if (getShip().getSpeed().lengthSquared() > 0) {
				force.scale(getShip().getSpeed().lengthSquared() / (2 * actualDistance + getShip().getSpeed().length()));
			}
			// getShip().debug4.copy(force);
		}
		// LOGGER.debug(force.length() + " - " + Ship.MAX_PROPULSOR_FORCE);
		float length = force.length();
		if (length > Ship.MAX_PROPULSOR_FORCE * getShip().getComponentsComposition().get(ComponentType.PROPULSORS)) {
			// LOGGER.debug("scaling ... ");
			force.scale(Ship.MAX_PROPULSOR_FORCE / length * getShip().getComponentsComposition().get(ComponentType.PROPULSORS));
		}

		// set orientation
		// TODO This is a very basic orientating method
		if (force != null && force.length() != 0) {
			float angle = (float) (force.angleWith(Vector2f.J) / Math.PI * 180);
			getShip().setRotate(angle);
		}
	}

	@Override
	public float getEnergyDt() {
		return conf.getFloatProperty(getClass().getCanonicalName() + ".energyDt"); //$NON-NLS-1$
	}

	@Override
	public Vector2f getForce() {
		return force;
	}

	@Override
	public void startBehavior() {
		getShip().setEnergy(getShip().getEnergy() + getEnergyDt());
		getShip().setResources(getShip().getResources() + getResourcesDt());

		setLastActivation(world.getTime());
		setActive(true);
		evalBehavior();

		// Apply force to ship
		getShip().getForceSources().add(this);
	}

}
