package net.carmgate.morph.services.behaviors;

import javax.inject.Inject;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.entities.components.prop.SimplePropulsor;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.ui.UIContext;
import net.carmgate.morph.ui.particles.ParticleEngine;
import net.carmgate.morph.ui.particles.ParticleSource;
import net.carmgate.morph.ui.renderers.MorphDebugDraw;
import net.carmgate.morph.ui.renderers.RenderMode;

public class SimplePropulsorBehavior extends ComponentBehavior<SimplePropulsor> {

	@Inject private Logger LOGGER;
	@Inject private Conf conf;
	@Inject private ParticleEngine particleEngine;
	@Inject private UIContext uiContext;
	@Inject private MorphDebugDraw debugDraw;

	private ParticleSource particleSource;

	private Vec2 toTargetVec = new Vec2();
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

		float targetDist = 350f;

		// target offset
		Vec2 tmp = null;
		Vec2 shipPos = cmp.getShip().getBody().getPosition();
		Vec2 targetPos = cmp.getTargetPosInWorld().mul(1f / 1000);
		Vec2 ttVec = targetPos.sub(shipPos).negate();
		Vec2 vVec = cmp.getShip().getBody().getLinearVelocity();
		Vec2 shipAppPoint = cmp.getShip().getBody().getWorldPoint(new Vec2(0, -0.1f));
		Vec2 shipAppVec = cmp.getShip().getBody().getWorldVector(new Vec2(0, -0.05f));
		Vec2 r1 = new Vec2((float) (Math.sqrt(3) / 2 * ttVec.x - 0.5f * ttVec.y), (float) (0.5f * ttVec.x + Math.sqrt(3) / 2 * ttVec.y));
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			debugDraw.drawSegment(targetPos, targetPos.add(r1), new Color3f(1, 0, 1));
			tmp = r1;
		}
		r1 = r1.mul(targetDist / 1000 / r1.length());
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			debugDraw.drawSegment(tmp.add(targetPos), r1.add(targetPos), new Color3f(0, 1, 0));
			tmp = r1;
		}
		r1 = r1.sub(vVec);
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			debugDraw.drawSegment(tmp.add(targetPos), r1.add(targetPos), new Color3f(1, 0, 0));
			tmp = r1;
		}
		r1 = r1.sub(shipAppVec);
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			debugDraw.drawSegment(tmp.add(targetPos), r1.add(targetPos), new Color3f(0, 1, 1));
		}

		float r = ttVec.length();

		float alpha = Math.max(0.2f, Math.min(1, 0.8f * (r * 1000 - targetDist) / targetDist + 0.2f));
		float fMax = Ship.MAX_PROPULSOR_FORCE * alpha;

		cmp.getForce().set(r1.sub(ttVec));
		cmp.getForce().set(cmp.getForce().mul(fMax / cmp.getForce().length()));
		if (uiContext.getRenderMode() == RenderMode.DEBUG) {
			debugDraw.drawSegment(shipAppPoint, shipAppPoint.add(cmp.getForce().mul(1000)), Color3f.BLUE);
			debugDraw.drawSegment(shipPos, shipPos.add(vVec), Color3f.RED);
		}

	}

	@Override
	public final void init(SimplePropulsor cmp) {
		cmp.setActive(true);

		// Apply force to ship
		cmp.getShip().getForceSources().add(cmp);

		// Create particle source
		// final Random random = new Random();
		// particleSource = new ParticleSource() {
		// @Override
		// public Particle createParticle() {
		// Ship playerShip = world.getPlayerShip();
		// float appearanceDistanceSq = playerShip.getPerceptionRadius() * playerShip.getPerceptionRadius();
		// float distanceToShipSq = cmp.getShip().getPos().distanceToSquared(playerShip.getPos());
		//
		// if (uiContext.getRenderMode() == RenderMode.DEBUG || distanceToShipSq < appearanceDistanceSq) {
		// Vector2f shipSpeed = cmp.getShip().getSpeed().clone();
		// Particle particle = new Particle();
		// particle.setBg(true);
		// float sameRandom = random.nextFloat();
		// float repX = shipSpeed.x * sameRandom / 8;
		// float repY = shipSpeed.y * sameRandom / 8;
		// particle.getPos().x = cmp.getShip().getPos().x + random.nextFloat() * 10 - 5 - repX;
		// particle.getPos().y = cmp.getShip().getPos().y + random.nextFloat() * 10 - 5 - repY;
		// particle.getSpeed().x = -shipSpeed.x;// + random.nextFloat() * 2;
		// particle.getSpeed().y = -shipSpeed.y;// + random.nextFloat() * 2;
		// particle.setRotation(cmp.getShip().getRotationTarget());
		// particle.setBirthTime(world.getTime());
		// particle.setDeathTime(world.getTime() + 100);
		// return particle;
		// }
		// return null;
		// }
		// };
		// particleSource.setBirthRate(1000);
		//
		// particleEngine.addParticleSource(particleSource);
	}

}
