package net.carmgate.morph.ai.impl;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.ai.AI;
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
			// LOGGER.debug("Activating laser");
			laser.startBehavior();
		} else {
			Vector2f shipPosToTarget = new Vector2f(ship.getPos()).sub(laser.getTarget().getPos());
			if (shipPosToTarget.lengthSquared() > laser.getRange() * laser.getRange()
					&& prop.isAvailable()) {

				// LOGGER.debug("Too far to activate lasers");
				Vector2f targetPos = new Vector2f(world.getPlayerShip().getPos());

				if (world.getPlayerShip().getSpeed().lengthSquared() > 0) {
					targetPos = world.getPlayerShip().getComponents().get(ComponentType.PROPULSORS).getTargetPosInWorld();
				}

				Vector2f toTarget = new Vector2f(targetPos).sub(ship.getPos());
				toTarget.scale((toTarget.length() - laser.getRange() + 10) / toTarget.length());
				if (toTarget.lengthSquared() > prop.getRange() * prop.getRange()) {
					toTarget.scale((prop.getRange() - 10) / toTarget.length());
					// LOGGER.debug("Too far to go to target with a single propulsor activation: " + toTarget.length());
				}
				Vector2f propTarget = toTarget.add(ship.getPos());
				prop.setTargetPosInWorld(propTarget);
				// LOGGER.debug("Activating propulsors");
				prop.startBehavior();
			}

			if (ship.getEnergy() < -laser.getEnergyDt()
					&& ship.getEnergy() < -prop.getEnergyDt()
					&& generator.canBeActivated()) {
				// LOGGER.debug("Activating generators");
				generator.startBehavior();
			}
		}
	}

}
