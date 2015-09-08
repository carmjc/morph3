package net.carmgate.morph.model.entities.physical.ship.components;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;

public abstract class Component implements Activable {
	private int id;
	private float energyDt = 0;
	private float resourcesDt = 0;
	private float integrityDt = 0;
	private boolean active;
	private boolean famished; // FIXME rename this
	private Animation animation;
	private final Holder<Ship> shipHolder = new Holder<>();
	private final Holder<PhysicalEntity> targetHolder = new Holder<>();
	private boolean useless;
	private Vector2f targetPosInWorld;
	private long lastActivation;

	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;

	public boolean canBeActivated() {
		return getShip().getEnergy() + getEnergyDt() >= 0 && getShip().getResources() + getResourcesDt() >= 0
				&& (getLastActivation() == 0 || getAvailability() >= 1)
				&& !isActive();
	}

	public Animation getAnimation() {
		return animation;
	}

	public float getAvailability() {
		String coolDown = conf.getProperty(getClass().getCanonicalName() + ".cooldown");
		if (coolDown == null) {
			return 1;
		}

		float value = ((float) world.getTime() - getLastActivation()) / conf.getIntProperty(getClass().getCanonicalName() + ".cooldown") / 1000;
		if (getLastActivation() == 0) {
			value = 1;
		}
		return value;
	}

	public final float getEnergyDt() {
		Float value = conf.getFloatProperty(getClass().getCanonicalName() + ".energyDt");
		if (value == null) {
			value = 0f;
		}
		return value;
	}

	public int getId() {
		return id;
	}

	public final float getIntegrityDt() {
		Float value = conf.getFloatProperty(getClass().getCanonicalName() + ".integrityDt");
		if (value == null) {
			value = 0f;
		}
		return value;
	}

	public long getLastActivation() {
		return lastActivation;
	}

	public final float getMaxStoredEnergy() {
		Float value = conf.getFloatProperty(getClass().getCanonicalName() + ".maxStoredEnergy");
		if (value == null) {
			value = 0f;
		}
		return value;
	}

	public final float getMaxStoredResources() {
		Float value = conf.getFloatProperty(getClass().getCanonicalName() + ".maxStoredResource");
		if (value == null) {
			value = 0f;
		}
		return value;
	}

	public final float getResourcesDt() {
		Float value = conf.getFloatProperty(getClass().getCanonicalName() + ".resourceDt");
		if (value == null) {
			value = 0f;
		}
		return value;
	}

	public Ship getShip() {
		return shipHolder.get();
	}

	public Holder<Ship> getShipHolder() {
		return shipHolder;
	}

	public PhysicalEntity getTarget() {
		return targetHolder.get();
	}

	public Holder<PhysicalEntity> getTargetHolder() {
		return targetHolder;
	}

	public Vector2f getTargetPosInWorld() {
		return targetPosInWorld;
	}

	public void initBehavior() {
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public boolean isFamished() {
		return famished;
	}

	public boolean isUseless() {
		return useless;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setFamished(boolean famished) {
		this.famished = famished;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastActivation(long lastActivation) {
		this.lastActivation = lastActivation;
	}

	public void setShip(Ship ship) {
		shipHolder.set(ship);
	}

	public void setTarget(PhysicalEntity target) {
		targetHolder.set(target);
		if (target != null) {
			targetPosInWorld = target.getPos();
		} else {
			targetPosInWorld = null;
		}
	}

	public void setTargetPosInWorld(Vector2f targetPosInWorld) {
		this.targetPosInWorld = targetPosInWorld;
	}

	public void setUseless(boolean useless) {
		this.useless = useless;
	}

	@Override
	public final void startBehavior() {
		LOGGER.debug("startBehavior for " + this);

		getShip().setEnergy(getShip().getEnergy() + getEnergyDt());
		getShip().setResources(getShip().getResources() + getResourcesDt());
		setLastActivation(world.getTime());

		initBehavior();
		evalBehavior();
	}

}
