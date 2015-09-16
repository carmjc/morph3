package net.carmgate.morph.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.calculator.Calculator;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.physical.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.physical.PhysicalEntityType;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentFactory;
import net.carmgate.morph.model.entities.physical.ship.components.Laser;
import net.carmgate.morph.model.entities.physical.ship.components.MiningLaser;
import net.carmgate.morph.model.entities.physical.ship.components.SimpleGenerator;
import net.carmgate.morph.model.entities.physical.ship.components.SimplePropulsor;

@Singleton
public class AiManager {

	@Inject private Logger LOGGER;
	@Inject private World world;
	@Inject private AI ai;
	@Inject private PhysicalEntityFactory physicalEntityFactory;
	@Inject private ComponentFactory componentFactory;
	@Inject private Calculator calculator;

	private long aiLastUpdateTime = 0;

	public void addWave() {
		float danger = Float.MAX_VALUE;
		List<Ship> ennemies = new ArrayList<>();
		while (danger > 1.5) {
			Ship ship = physicalEntityFactory.newInstance(PhysicalEntityType.SHIP);
			ship.getPos().copy(new Random().nextInt(1000) - 500, new Random().nextInt(800) - 400);
			ship.setPlayer(world.getPlayers().get("Other")); //$NON-NLS-1$
			ship.setMass(0.5f);
			ship.setEnergy(20);
			ship.setResources(20);
			ship.setIntegrity(1);
			ship.setDurability(1);
			ship.setRotation(new Random().nextFloat() * 360);
			ship.add(componentFactory.newInstance(Laser.class));
			ship.add(componentFactory.newInstance(SimplePropulsor.class));
			ship.add(componentFactory.newInstance(SimpleGenerator.class));
			ship.add(componentFactory.newInstance(MiningLaser.class));
			ship.create();
			ennemies.add(ship);
			danger = calculator.computeDanger(world.getPlayerShip(), ennemies);
			LOGGER.debug("Danger (" + ennemies.size() + "): " + danger); //$NON-NLS-1$ //$NON-NLS-2$
			if (danger > 1.5) {
				world.add(ship);
			}
		}
	}

	public void execute() {
		if (world.getTime() - aiLastUpdateTime > 500) {
			for (Ship ship : world.getShips()) {
				if ("Other".equals(ship.getPlayer().getName())) {
					ai.run(ship);
				}
			}
			aiLastUpdateTime = world.getTime();
		}
	}
}
