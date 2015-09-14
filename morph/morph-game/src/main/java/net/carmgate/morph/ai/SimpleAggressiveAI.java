package net.carmgate.morph.ai;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;

public class SimpleAggressiveAI implements AI {

	@Inject private Logger LOGGER;
	@Inject private World world;

	@Override
	public void run(Ship ship) {
		Component laser = ship.getComponents().get(ComponentType.LASERS);
		Component generator = ship.getComponents().get(ComponentType.GENERATORS);
		Component prop = ship.getComponents().get(ComponentType.PROPULSORS);
		laser.setTarget(world.getPlayerShip());
		if (laser.canBeActivated()) {
			LOGGER.debug("laser");
			laser.startBehavior();
		} else {
			Vector2f shipPosToTarget = new Vector2f(ship.getPos()).sub(laser.getTarget().getPos());
			if (shipPosToTarget.lengthSquared() > laser.getRange() * laser.getRange()) {
				LOGGER.debug("too far: ");
				Vector2f targetPos = new Vector2f(world.getPlayerShip().getPos());
				Vector2f toTarget = new Vector2f(targetPos).sub(ship.getPos());
				if (toTarget.lengthSquared() > prop.getRange() * prop.getRange()) {
					toTarget.scale((prop.getRange() - 10) / toTarget.length());
					LOGGER.debug("indirect: " + toTarget.length());
				}
				Vector2f propTarget = toTarget.add(ship.getPos());
				prop.setTargetPosInWorld(propTarget);
				if (prop.canBeActivated()) {
					LOGGER.debug("prop");
					prop.startBehavior();
				}
			}

			if (ship.getEnergy() < -laser.getEnergyDt()
					&& ship.getEnergy() < -prop.getEnergyDt()
					&& generator.canBeActivated()) {
				LOGGER.debug("generator");
				generator.startBehavior();
			}
		}
	}

}
