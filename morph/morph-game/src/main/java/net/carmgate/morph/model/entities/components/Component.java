package net.carmgate.morph.model.entities.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.model.Holder;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.parts.HardPart;
import net.carmgate.morph.model.entities.parts.Part;
import net.carmgate.morph.model.entities.parts.SoftPart;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.model.geometry.Vector2f;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Component implements Activable {
	public static final float SCALE = 3.292f * 2;
	private static final Logger LOGGER = LoggerFactory.getLogger(Component.class);

	@Transient protected MEventManager eventManager;

	@Id private int id;
	private Vector2f posInShip = new Vector2f();
	@Transient private boolean active;
	@Transient private Animation animation;
	@Transient private final Holder<Ship> shipHolder = new Holder<>();
	@Transient private final Holder<PhysicalEntity> targetHolder = new Holder<>();
	private Vector2f targetPosInWorld;
	private long lastActivation;
	private float[] color;
	@ManyToOne private Ship ship;

	@Transient private final List<HardPart> hardParts = new ArrayList<>();
	@Transient private final List<SoftPart> softParts = new ArrayList<>();

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

	public Animation getAnimation() {
		return animation;
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

	@Override
	public boolean isActive() {
		return active;
	}

	public boolean isPosWithinRange(Vector2f pos) {
		return pos != null && (getRange() == 0 || pos.distanceToSquared(getShip().getPos()) <= getRange() * getRange());
	}

	@PostLoad
	@PostConstruct
	protected void postLoad() {
		shipHolder.set(ship);
		eventManager = MEventManager.getInstance();
		eventManager.scanAndRegister(this);
	}

	@PrePersist
	private void prePersist() {
		ship = shipHolder.get();
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

	public void setColor(float[] color) {
		this.color = color;
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

}
