package net.carmgate.morph.model.entities.ship;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAll", query = "from Ship")
})
public class Ship extends PhysicalEntity {

	public static final float MAX_PROPULSOR_FORCE = 200f;

	@ManyToOne(cascade = CascadeType.ALL) private Player owner;
	private float durability;
	@OneToMany(cascade = CascadeType.ALL) private final Map<ComponentType, Component> components = new HashMap<>();

	// internal economics
	private float energy;
	private float energyMax;
	private float resources;
	private float resourcesMax;
	private float integrity = 1;
	private int xp;
	private float maxDamageDt = 0;
	private float maxDefenseDt = 0;

	@Transient public Vector2f debug1 = new Vector2f();
	@Transient public Vector2f debug2 = new Vector2f();
	@Transient public Vector2f debug3 = new Vector2f();
	@Transient public Vector2f debug4 = new Vector2f();
	@Transient private boolean forceStop;
	private long creationTime;

	private int softSpaceMax;
	private int hardSpaceMax;
	private Integer xpMax;
	@Transient private Float perceptionRadius;

	@Transient private MEventManager eventManager;

	public void add(Component component) {
		component.setShip(this);
		ComponentKind componentKind = component.getClass().getAnnotation(ComponentKind.class);
		components.put(componentKind.value(), component);
	}

	public Map<ComponentType, Component> getComponents() {
		return components;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public float getDurability() {
		return durability;
	}

	public float getEnergy() {
		return energy;
	}

	public float getEnergyMax() {
		return energyMax;
	}

	public int getHardSpaceMax() {
		return hardSpaceMax;
	}

	public float getIntegrity() {
		return integrity;
	}

	public float getMaxDamageDt() {
		return maxDamageDt;
	}

	public float getMaxDefenseDt() {
		return maxDefenseDt;
	}

	public Float getPerceptionRadius() {
		return perceptionRadius;
	}

	public Player getPlayer() {
		return owner;
	}

	public float getResources() {
		return resources;
	}

	public float getResourcesMax() {
		return resourcesMax;
	}

	public int getSoftSpaceMax() {
		return softSpaceMax;
	}

	public int getXp() {
		return xp;
	}

	public int getXpMax() {
		return xpMax;
	}

	public boolean isForceStop() {
		return forceStop;
	}

	@PostLoad
	private void postLoad() {
		eventManager = MEventManager.getInstance();
		eventManager.scanAndRegister(this);
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setDurability(float durability) {
		this.durability = durability;
	}

	public void setEnergy(float energy) {
		this.energy = energy;
	}

	public void setEnergyMax(float energyMax) {
		this.energyMax = energyMax;
	}

	public void setForceStop(boolean forceStop) {
		this.forceStop = forceStop;
	}

	public void setHardSpaceMax(int hardSpaceMax) {
		this.hardSpaceMax = hardSpaceMax;
	}

	public void setIntegrity(float integrity) {
		this.integrity = integrity;
	}

	public void setMaxDamageDt(float maxDamageDt) {
		this.maxDamageDt = maxDamageDt;
	}

	public void setMaxDefenseDt(float maxDefenseDt) {
		this.maxDefenseDt = maxDefenseDt;
	}

	public void setPerceptionRadius(Float perceptionRadius) {
		this.perceptionRadius = perceptionRadius;
	}

	public void setPlayer(Player owner) {
		this.owner = owner;
	}

	public void setResources(float resources) {
		this.resources = resources;
	}

	public void setResourcesMax(float resourcesMax) {
		this.resourcesMax = resourcesMax;
	}

	public void setSoftSpaceMax(int softSpaceMax) {
		this.softSpaceMax = softSpaceMax;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public void setXpMax(Integer xpMax) {
		this.xpMax = xpMax;
	}
}
