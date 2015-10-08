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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.lwjgl.util.vector.Vector2f;

import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.model.Player;
import net.carmgate.morph.model.entities.PhysicalEntity;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.geometry.Vec2;
import net.carmgate.morph.model.geometry.Vector3f;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAll", query = "from Ship")
})
public class Ship extends PhysicalEntity {

	public static final float MAX_PROPULSOR_FORCE = 0.001f;

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

	@Transient public Vec2 debug1 = new Vec2();
	@Transient public Vec2 debug2 = new Vec2();
	@Transient public Vec2 debug3 = new Vec2();
	@Transient public Vec2 debug4 = new Vec2();
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

	@Override
	public BodyDef getBodyDef() {
		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.DYNAMIC;
		bd.linearDamping = 0.5f;
		bd.angularDamping = 10f;
		return bd;
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

	@Override
	public FixtureDef getFixtureDef(CircleShape cs) {
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 5f;
		fd.friction = 0;
		fd.restitution = 0;
		// fd.filter.groupIndex = -1;
		return fd;
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

	@Override
	public CircleShape getShape() {
		CircleShape cs = new CircleShape();
		cs.m_radius = 0.025f;
		return cs;
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

		getModelToWorld().setIdentity();
		getModelToWorld().translate(new Vector2f(getPosition().x, getPosition().y), getModelToWorld());
		getModelToWorld().rotate(getBody().getAngle(), Vector3f.Z, getModelToWorld());
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
