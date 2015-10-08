package net.carmgate.morph.ai.impl;

import javax.inject.Inject;

import org.jbox2d.common.Vec2;
import org.slf4j.Logger;

import net.carmgate.morph.ai.AI;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;

public class SimpleAggressiveAI implements AI {

	@Inject private Logger LOGGER;
	@Inject private MWorld world;
	@Inject private ComponentManager componentManager;

	@Override
	public void run(Ship ship) {
		Component laser = ship.getComponents().get(ComponentType.LASERS);
		Component generator = ship.getComponents().get(ComponentType.GENERATORS);
		Component prop = ship.getComponents().get(ComponentType.PROPULSORS);
		laser.setTarget(world.getPlayerShip());
		if (componentManager.canBeActivated(laser)) {
			// LOGGER.debug("Activating laser");
			componentManager.startBehavior(laser);
		} else {
			Vec2 shipPosToTarget = new org.jbox2d.common.Vec2(ship.getPosition()).sub(laser.getTarget().getPosition());
			if (shipPosToTarget.lengthSquared() > laser.getRange() * laser.getRange()
					&& componentManager.isAvailable(prop)) {

				// LOGGER.debug("Too far to activate lasers");
				Vec2 targetPos = new Vec2(world.getPlayerShip().getPosition());

				if (world.getPlayerShip().getBody().getLinearVelocity().lengthSquared() > 0) {
					// targetPos = world.getPlayerShip().getComponents().get(ComponentType.PROPULSORS).getTargetPosInWorld();
				}

				Vec2 toTarget = new Vec2(targetPos).sub(ship.getPosition());
				toTarget.mul((toTarget.length() - laser.getRange() + 10) / toTarget.length());
				if (toTarget.lengthSquared() > prop.getRange() * prop.getRange()) {
					toTarget.mul((prop.getRange() - 10) / toTarget.length());
					// LOGGER.debug("Too far to go to target with a single propulsor activation: " + toTarget.length());
				}
				Vec2 propTarget = toTarget.add(ship.getPosition());
				prop.setTargetPosInWorld(propTarget);
				// LOGGER.debug("Activating propulsors");
				componentManager.startBehavior(prop);
			}

			if (ship.getEnergy() < -laser.getEnergyDt()
					&& ship.getEnergy() < -prop.getEnergyDt()
					&& componentManager.canBeActivated(generator)) {
				// LOGGER.debug("Activating generators");
				componentManager.startBehavior(generator);
			}
		}
	}

}
