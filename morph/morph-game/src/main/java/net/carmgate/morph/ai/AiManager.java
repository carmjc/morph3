package net.carmgate.morph.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.calculator.Calculator;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.PhysicalEntityFactory;
import net.carmgate.morph.model.entities.components.ComponentFactory;
import net.carmgate.morph.model.entities.components.generator.SimpleGenerator;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.services.ComponentManager;
import net.carmgate.morph.services.ShipManager;
import net.carmgate.morph.services.WorldManager;

@Singleton
public class AiManager {

	@Inject private Logger LOGGER;
	@Inject private World world;
	@Inject private AI ai;
	@Inject private PhysicalEntityFactory physicalEntityFactory;
	@Inject private ComponentFactory componentFactory;
	@Inject private Calculator calculator;
	@Inject private ShipManager shipManager;
	@Inject private ComponentManager componentManager;
	@Inject private WorldManager worldManager;

	public void addWave() {
		float danger = Float.MAX_VALUE;
		List<Ship> ennemies = new ArrayList<>();
		while (danger > 2) {
			Ship ship = physicalEntityFactory.newInstance(Ship.class);
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
			shipManager.init(ship);
			ennemies.add(ship);
			danger = calculator.computeDanger(world.getPlayerShip(), ennemies);
			LOGGER.debug("Wave global danger (with " + ennemies.size() + " ships): " + danger); //$NON-NLS-1$ //$NON-NLS-2$
			if (danger > 2) {
				worldManager.add(ship);
			}
		}
	}

	public void execute() {

		if (world.getTime() - world.getAiLastUpdate() > 500) {
			for (Ship ship : world.getShips()) {
				if ("Other".equals(ship.getPlayer().getName())) {
					ai.run(ship);
				}
			}
			world.setAiLastUpdate(world.getTime());
		}
	}
}
