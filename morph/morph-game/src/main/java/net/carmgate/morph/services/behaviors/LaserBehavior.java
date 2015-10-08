package net.carmgate.morph.services.behaviors;

import javax.inject.Inject;

import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.ship.Ship;

public class LaserBehavior extends ComponentBehavior<Laser> {

	@Inject private MWorld world;

	@Override
	public void eval(Laser cmp) {
		if (world.getTime() - cmp.getLastActivation() > cmp.getCooldown() * 1000) {
			cmp.setActive(false);

			// Remove health to target
			Ship targetShip = (Ship) cmp.getTarget();
			targetShip.setIntegrity(
					targetShip.getIntegrity() - cmp.getDamage() / targetShip.getDurability());
		}
	}

	@Override
	public void init(Laser cmp) {
		if (cmp.getTarget() instanceof Ship) {
			cmp.setActive(true);
		}
	}

}
