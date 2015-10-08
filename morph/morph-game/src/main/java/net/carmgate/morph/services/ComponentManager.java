package net.carmgate.morph.services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.component.ComponentLoaded;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.model.animations.ship.MiningLaserAnim;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.entities.components.mining.MiningLaser;
import net.carmgate.morph.model.entities.components.offensive.Laser;
import net.carmgate.morph.model.entities.parts.HardPart;
import net.carmgate.morph.model.entities.parts.PartFactory;
import net.carmgate.morph.model.entities.parts.SoftPart;
import net.carmgate.morph.model.entities.parts.hardParts.OverClocking;
import net.carmgate.morph.services.behaviors.ComponentBehavior;

@Singleton
public class ComponentManager {

	@Inject private Logger LOGGER;
	@Inject private MWorld world;
	@Inject private Conf conf;
	@Inject private MEventManager eventManager;
	@Inject private AnimationFactory animationFactory;
	@Inject private PartFactory partFactory;
	@Inject private Instance<ComponentBehavior<? extends Component>> componentBehaviorInstances;

	private final Map<Class, ComponentBehavior> behaviorMap = new HashMap<>();

	public boolean canBeActivated(Component cmp) {
		return cmp.hasEnoughResources()
				&& isAvailable(cmp)
				&& (!cmp.getClass().isAnnotationPresent(NeedsTarget.class)
						|| cmp.isPosWithinRange(cmp.getTargetPosInWorld()));
	}

	public void evalBehavior(Component cmp) {
		behaviorMap.get(cmp.getClass()).eval(cmp);
	}

	public float getAvailability(Component cmp) {
		if (cmp.getCooldown() == 0) {
			return 1;
		}

		float value = ((float) world.getTime() - cmp.getLastActivation()) / cmp.getCooldown() / 1000;
		if (cmp.getLastActivation() == 0) {
			value = 1;
		}
		return value;
	}

	public void init(Component cmp) {
		cmp.setColor(conf.getFloatArrayProperty(cmp.getClass().getCanonicalName() + ".color"));
		cmp.setDamage(conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".target.damage"));

		Float cooldown = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".cooldown");
		if (cooldown == null) {
			cooldown = 0f;
		}
		cmp.setCooldown(cooldown);

		Float durability = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".durabilityDt");
		if (durability == null) {
			durability = 0f;
		}
		cmp.setDurability(durability);

		Float energyDt = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".energyDt");
		if (energyDt == null) {
			energyDt = 0f;
		}
		cmp.setEnergyDt(energyDt);

		Float integrity = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".integrityDt");
		if (integrity == null) {
			integrity = 0f;
		}
		cmp.setIntegrity(integrity);

		Float maxStoredEnergy = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".maxStoredEnergy");
		if (maxStoredEnergy == null) {
			maxStoredEnergy = 0f;
		}
		cmp.setMaxStoredEnergy(maxStoredEnergy);

		Float maxStoredResources = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".maxStoredResources");
		if (maxStoredResources == null) {
			maxStoredResources = 0f;
		}
		cmp.setMaxStoredResources(maxStoredResources);

		Float range = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".range");
		if (range == null) {
			range = 0f;
		}
		cmp.setRange(range);

		Float resourceDt = conf.getFloatProperty(cmp.getClass().getCanonicalName() + ".resourceDt");
		if (resourceDt == null) {
			resourceDt = 0f;
		}
		cmp.setResourceDt(resourceDt);

		updateComponentWithEffectOfParts(cmp);
	}

	public boolean isAvailable(Component cmp) {
		return (cmp.getLastActivation() == 0 || getAvailability(cmp) >= 1) && !cmp.isActive();
	}

	@SuppressWarnings("unused")
	private void onComponentLoaded(@MObserves ComponentLoaded componentLoaded) {
		Component cmp = componentLoaded.getComponent();
		if (cmp instanceof Laser) {
			LaserAnim laserAnim = animationFactory.newInstance(LaserAnim.class);
			laserAnim.setSource(cmp);
			laserAnim.setTargetHolder(cmp.getTargetHolder());
			cmp.setAnimation(laserAnim); // TODO probably useless
			world.addAnimation(laserAnim);

			// FIXME test code, this should be rendered completely dynamic
			OverClocking overClocking = partFactory.newInstance(OverClocking.class);
			cmp.addPart(overClocking);
		} else if (cmp instanceof MiningLaser) {
			MiningLaserAnim laserAnim = animationFactory.newInstance(MiningLaserAnim.class);
			laserAnim.setSource(cmp);
			laserAnim.setTarget(cmp.getTargetHolder());
			cmp.setAnimation(laserAnim);
			world.addAnimation(laserAnim);
		}
	}

	@PostConstruct
	private void postConstruct() {
		eventManager.scanAndRegister(this);

		componentBehaviorInstances.forEach((cb) -> {
			Class<? extends Component> cmpClass = (Class<? extends Component>) ((java.lang.reflect.ParameterizedType) cb.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			behaviorMap.put(cmpClass, cb);
		});
	}

	public final void startBehavior(Component cmp) {
		cmp.getShip().setEnergy(cmp.getShip().getEnergy() + cmp.getEnergyDt());
		cmp.getShip().setResources(cmp.getShip().getResources() + cmp.getResourcesDt());
		cmp.getShip().setIntegrity(cmp.getShip().getIntegrity() + cmp.getDurabilityDt() / cmp.getShip().getDurability());
		cmp.setLastActivation(world.getTime());

		ComponentBehavior<Component> evaluator = behaviorMap.get(cmp.getClass());
		if (evaluator != null) {
			evaluator.init(cmp);
			if (cmp.isActive()) {
				evaluator.eval(cmp);
			}
		}
	}

	private void updateComponentWithEffectOfParts(Component cmp) {
		for (SoftPart softPart : cmp.getSoftParts()) {
			softPart.computeEffectOnComponent(cmp);
		}

		for (HardPart hardPart : cmp.getHardParts()) {
			hardPart.computeEffectOnComponent(cmp);
		}
	}

}
