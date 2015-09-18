package net.carmgate.morph.model.entities.physical.ship.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
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
	public static final float SCALE = 3.292f * 2;

	private int id;
	private Vector2f posInShip = new Vector2f();
	private boolean active;
	private Animation animation;
	private final Holder<Ship> shipHolder = new Holder<>();
	private final Holder<PhysicalEntity> targetHolder = new Holder<>();
	private Vector2f targetPosInWorld;
	private long lastActivation;
	private float[] color;

	private final List<HardPart> hardParts = new ArrayList<>();
	private final List<SoftPart> softParts = new ArrayList<>();

	@Inject private World world;
	@Inject private Conf conf;
	@Inject private Logger LOGGER;

	private Float cooldown;
	private Float damage;
	private Float durability;
	private Float energyDt;
	private Float integrity;
	private Float maxStoredEnergy;
	private Float maxStoredResources;
	private Float range;
	private Float resourceDt;

	public boolean addPart(Part part) {
		if (part instanceof HardPart) {
			part.setComponent(this);
			return hardParts.add((HardPart) part);
		}
		if (part instanceof SoftPart) {
			part.setComponent(this);
			return softParts.add((SoftPart) part);
		}
		return false;
	}

	public boolean canBeActivated() {
		return hasEnoughResources()
				&& isAvailable()
				&& (!getClass().isAnnotationPresent(NeedsTarget.class)
						|| isPosWithinRange(targetPosInWorld));
	}

	public Animation getAnimation() {
		return animation;
	}

	public float getAvailability() {
		if (getCooldown() == 0) {
			return 1;
		}

		float value = ((float) world.getTime() - getLastActivation()) / getCooldown() / 1000;
		if (getLastActivation() == 0) {
			value = 1;
		}
		return value;
	}

	public float[] getColor() {
		return color;
	}

	public float getCooldown() {
		return cooldown;
	}

	public Float getDamage() {
		return damage;
	}

	public Float getDurability() {
		return durability;
	}

	public final float getDurabilityDt() {
		return durability;
	}

	public final float getEnergyDt() {
		return energyDt;
	}

	public List<HardPart> getHardParts() {
		return hardParts;
	}

	public int getId() {
		return id;
	}

	public Float getIntegrity() {
		return integrity;
	}

	public long getLastActivation() {
		return lastActivation;
	}

	public final float getMaxStoredEnergy() {
		return maxStoredEnergy;
	}

	public final float getMaxStoredResources() {
		return maxStoredResources;
	}

	public Vector2f getPosInShip() {
		return posInShip;
	}

	public final float getRange() {
		return range;
	}

	public Float getResourceDt() {
		return resourceDt;
	}

	public final float getResourcesDt() {
		return resourceDt;
	}

	public Ship getShip() {
		return shipHolder.get();
	}

	public Holder<Ship> getShipHolder() {
		return shipHolder;
	}

	public List<SoftPart> getSoftParts() {
		return softParts;
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

	public boolean hasEnoughResources() {
		return getShip().getEnergy() + getEnergyDt() >= 0 && getShip().getResources() + getResourcesDt() >= 0;
	}

	@PostConstruct
	public void init() {
		color = conf.getFloatArrayProperty(getClass().getCanonicalName() + ".color");
		damage = conf.getFloatProperty(getClass().getCanonicalName() + ".target.damage");

		cooldown = conf.getFloatProperty(getClass().getCanonicalName() + ".cooldown");
		if (cooldown == null) {
			cooldown = 0f;
		}

		durability = conf.getFloatProperty(getClass().getCanonicalName() + ".durabilityDt");
		if (durability == null) {
			durability = 0f;
		}

		energyDt = conf.getFloatProperty(getClass().getCanonicalName() + ".energyDt");
		if (energyDt == null) {
			energyDt = 0f;
		}

		integrity = conf.getFloatProperty(getClass().getCanonicalName() + ".integrityDt");
		if (integrity == null) {
			integrity = 0f;
		}

		maxStoredEnergy = conf.getFloatProperty(getClass().getCanonicalName() + ".maxStoredEnergy");
		if (maxStoredEnergy == null) {
			maxStoredEnergy = 0f;
		}

		maxStoredResources = conf.getFloatProperty(getClass().getCanonicalName() + ".maxStoredResources");
		if (maxStoredResources == null) {
			maxStoredResources = 0f;
		}

		range = conf.getFloatProperty(getClass().getCanonicalName() + ".range");
		if (range == null) {
			range = 0f;
		}

		resourceDt = conf.getFloatProperty(getClass().getCanonicalName() + ".resourceDt");
		if (resourceDt == null) {
			resourceDt = 0f;
		}

		updateComponentWithEffectOfParts();
	}

	public void initBehavior() {
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public boolean isAvailable() {
		return (getLastActivation() == 0 || getAvailability() >= 1) && !isActive();
	}

	public boolean isPosWithinRange(Vector2f pos) {
		return pos != null && (getRange() == 0 || pos.clone().sub(getShip().getPos()).lengthSquared() <= getRange() * getRange());
	}

	public boolean removePart(Part part) {
		if (part instanceof HardPart) {
			return hardParts.remove(part);
		}
		if (part instanceof SoftPart) {
			return softParts.remove(part);
		}
		return false;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public void setDamage(Float damage) {
		this.damage = damage;
	}

	public void setDurability(Float durability) {
		this.durability = durability;
	}

	public void setEnergyDt(Float energyDt) {
		this.energyDt = energyDt;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIntegrity(Float integrity) {
		this.integrity = integrity;
	}

	public void setLastActivation(long lastActivation) {
		this.lastActivation = lastActivation;
	}

	public void setMaxStoredEnergy(Float maxStoredEnergy) {
		this.maxStoredEnergy = maxStoredEnergy;
	}

	public void setMaxStoredResources(Float maxStoredResources) {
		this.maxStoredResources = maxStoredResources;
	}

	public void setRange(Float range) {
		this.range = range;
	}

	public void setResourceDt(Float resourceDt) {
		this.resourceDt = resourceDt;
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

	@Override
	public final void startBehavior() {
		getShip().setEnergy(getShip().getEnergy() + getEnergyDt());
		getShip().setResources(getShip().getResources() + getResourcesDt());
		getShip().setIntegrity(getShip().getIntegrity() + getDurabilityDt() / getShip().getDurability());
		setLastActivation(world.getTime());

		initBehavior();
		evalBehavior();
	}

	private void updateComponentWithEffectOfParts() {
		for (SoftPart softPart : getSoftParts()) {
			softPart.computeEffectOnComponent(this);
		}

		for (HardPart hardPart : getHardParts()) {
			hardPart.computeEffectOnComponent(this);
		}
	}

}
