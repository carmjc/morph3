package net.carmgate.morph.model.entities.physical.ship;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.entities.ship.ShipComponentsUpdated;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.XPHolder;
import net.carmgate.morph.model.entities.Surroundings;
import net.carmgate.morph.model.entities.physical.PhysicalEntity;
import net.carmgate.morph.model.entities.physical.ship.components.Component;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentKind;
import net.carmgate.morph.model.entities.physical.ship.components.ComponentType;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.script.util.ScriptManager;

public class Ship extends PhysicalEntity {

	public static final float MAX_PROPULSOR_FORCE = 200f;

	@Inject private ScriptManager scriptManager;
	@Inject private XPHolder xpHolder;
	@Inject private Conf conf;
	@Inject private World world;
	@Inject private Logger LOGGER;

	private Player owner;
	@Deprecated private final Surroundings surroundings = new Surroundings();
	private float durability;
	private final Map<ComponentType, Component> components = new HashMap<>();

	// internal economics
	private float energy;
	private float energyMax;
	private float resources;
	private float resourcesMax;
	private float integrity = 1;
	private int xp;

	private float maxDamageDt = 0;
	private float maxDefenseDt = 0;

	public Vector2f debug1 = new Vector2f();
	public Vector2f debug2 = new Vector2f();
	public Vector2f debug3 = new Vector2f();
	public Vector2f debug4 = new Vector2f();
	private boolean forceStop;
	private long creationTime;

	private int softSpaceMax;
	private int hardSpaceMax;
	private Integer xpMax;

	public void add(Component component) {
		component.setShip(this);
		ComponentKind componentKind = component.getClass().getAnnotation(ComponentKind.class);
		components.put(componentKind.value(), component);
	}

	public void computeMaxDamageDt() {
		Component laser = getComponents().get(ComponentType.LASERS);
		if (laser == null) {
			maxDamageDt = 0f;
			return;
		}
		maxDamageDt = laser.getDamage() / laser.getCooldown();
	}

	public void computeMaxDefenseDt() {
		Component repairer = getComponents().get(ComponentType.REPAIRER);
		if (repairer == null) {
			maxDefenseDt = 0;
			return;
		}
		maxDefenseDt = repairer.getDurabilityDt() / repairer.getCooldown();
	}

	/**
	 * Call this method to materialize the ship
	 */
	public void create() {
		creationTime = world.getTime();

		computeMaxDamageDt();
		computeMaxDefenseDt();

		// conf
		xpMax = conf.getIntProperty("xp.max");
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

	@Deprecated
	public Surroundings getSurroundings() {
		return surroundings;
	}

	public int getXp() {
		return xp;
	}

	public int getXpMax() {
		return xpMax;
	}

	@PostConstruct
	private void init() {
		xpHolder.setShip(this);
	}

	public boolean isForceStop() {
		return forceStop;
	}

	@SuppressWarnings("unused")
	private void onShipComponentsUpdated(@MObserves ShipComponentsUpdated shipComponentsUpdated) {
		computeMaxDamageDt();
		computeMaxDefenseDt();
	}

	@SuppressWarnings("unused")
	private void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (shipDeath.getShip() != this) {
			HashMap<String, Object> inputs = new HashMap<>();
			inputs.put("self", this);
			inputs.put("ship", new ReadOnlyShip(shipDeath.getShip()));
			scriptManager.callScript("onOtherShipDeath", getPlayer(), inputs, null);
		}
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
}
