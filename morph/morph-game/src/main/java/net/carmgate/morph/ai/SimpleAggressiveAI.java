package net.carmgate.morph.ai;

import javax.inject.Inject;

import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;

public class SimpleAggressiveAI implements AI {

	@Inject private World world;

	@Override
	public void run(Ship ship) {
		Component laser = ship.getComponents().get(ComponentType.LASERS);
		Component generator = ship.getComponents().get(ComponentType.GENERATORS);
		if (laser.canBeActivated()) {
			laser.setTarget(world.getPlayerShip());
			laser.startBehavior();
		} else if (generator.canBeActivated()) {
			generator.startBehavior();
		}
	}

}
