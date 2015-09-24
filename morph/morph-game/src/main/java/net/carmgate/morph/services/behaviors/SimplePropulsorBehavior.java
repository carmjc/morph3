package net.carmgate.morph.services.behaviors;

import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.particles.Particle;
import net.carmgate.morph.ui.particles.ParticleEngine;
import net.carmgate.morph.ui.particles.ParticleSource;
import net.carmgate.morph.ui.renderers.RenderMode;

public class SimplePropulsorBehavior extends ComponentBehavior<SimplePropulsor> {

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private ParticleEngine particleEngine;
	@Inject private World world;
	@Inject private UIContext uiContext;

	private ParticleSource particleSource;

	private Vector2f tmpVect = new Vector2f();

	private void endBehavior(SimplePropulsor cmp) {
		cmp.setActive(false);
		cmp.setTarget(null);

		particleEngine.removeParticleSource(particleSource);
		particleSource = null;
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

		// Apply force to ship
		cmp.getShip().getForceSources().add(cmp);

		// Create particle source
		final Random random = new Random();
		particleSource = new ParticleSource() {
			@Override
			public Particle createParticle() {
				Ship playerShip = world.getPlayerShip();
				float appearanceDistanceSq = playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius();
				float distanceToShipSq = cmp.getShip().getPos().distanceToSquared(playerShip.getPos());

				if (uiContext.getRenderMode() == RenderMode.DEBUG || distanceToShipSq < appearanceDistanceSq) {
					Vector2f shipSpeed = cmp.getShip().getSpeed().clone();
					Particle particle = new Particle();
					particle.setBg(true);
					float sameRandom = random.nextFloat();
					float repX = shipSpeed.x * sameRandom / 8;
					float repY = shipSpeed.y * sameRandom / 8;
					particle.getPos().x = cmp.getShip().getPos().x + random.nextFloat() * 10 - 5 - repX;
					particle.getPos().y = cmp.getShip().getPos().y + random.nextFloat() * 10 - 5 - repY;
					particle.getSpeed().x = -shipSpeed.x;// + random.nextFloat() * 2;
					particle.getSpeed().y = -shipSpeed.y;// + random.nextFloat() * 2;
					particle.setRotation(cmp.getShip().getRotationTarget());
					particle.setBirthTime(world.getTime());
					particle.setDeathTime(world.getTime() + 100);
					return particle;
				}
				return null;
			}
		};
		particleSource.setBirthRate(1000);

		particleEngine.addParticleSource(particleSource);
	}

}
