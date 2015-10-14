package net.carmgate.morph.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbox2d.common.Vec2;
import org.slf4j.Logger;

import net.carmgate.morph.calculator.Calculator;
import net.carmgate.morph.model.MWorld;
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
	@Inject private MWorld world;
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
			ship.setPlayer(world.getPlayers().get("Other")); //$NON-NLS-1$
			ship.setEnergy(20);
			ship.setResources(20);
			ship.setIntegrity(1);
			ship.setDurability(1);
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
				ship.getBody().setTransform(new Vec2(new Random().nextFloat() * 0.5f + 0f, new Random().nextFloat() * 0.5f + 0f),
						(float) (new Random().nextFloat() * 2 * Math.PI));
				// ship.getBody().setTransform(new Vec2(0.7f, 0.5f),
				// (float) (new Random().nextFloat() * 2 * Math.PI));
				LOGGER.debug("ai initial position: " + ship.getPosition());
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
