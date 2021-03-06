package net.carmgate.morph.services;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import net.carmgate.morph.Messages;
import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.MEventManager;
import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.world.entities.ship.ShipComponentsUpdated;
import net.carmgate.morph.events.world.entities.ship.ShipDeath;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.Player.PlayerType;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;

@Singleton
public class ShipManager {

	@Inject private Conf conf;
	@Inject private MWorld world;
	@Inject private Logger LOGGER;
	@Inject private MEventManager eventManager;
	@Inject private AnimationFactory animationFactory;
	@Inject private MessageManager messageManager;
	@Inject private Messages messages;

	public void computeMaxDamageDt(Ship ship) {
		Component laser = ship.getComponents().get(ComponentType.LASERS);
		if (laser == null) {
			ship.setMaxDamageDt(0f);
			return;
		}
		ship.setMaxDamageDt(laser.getDamage() / laser.getCooldown());
	}

	public void computeMaxDefenseDt(Ship ship) {
		Component repairer = ship.getComponents().get(ComponentType.REPAIRER);
		if (repairer == null) {
			ship.setMaxDefenseDt(0);
			return;
		}
		ship.setMaxDefenseDt(repairer.getDurabilityDt() / repairer.getCooldown());
	}


	@PostConstruct
	private void init() {
		eventManager.scanAndRegister(this);
	}

	public void init(Ship ship) {
		ship.setCreationTime(world.getTime());

		computeMaxDamageDt(ship);
		computeMaxDefenseDt(ship);

		// conf
		ship.setXpMax(conf.getIntProperty("xp.max"));
		ship.setPerceptionRadius(conf.getFloatProperty("ship.perceptionRadius"));

		int shipType = 0;
		int propIndex = 0;
		int turretIndex = 0;
		int coreIndex = 0;
		for (Component cmp : ship.getComponents().values()) {
			if (cmp.getPosInShip().lengthSquared() == 0) {
				Float compX;
				Float compY;
				if (cmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.PROPULSORS) {
					compX = conf.getFloatProperty("ship." + shipType + ".comps.prop." + propIndex + ".x");
					compY = conf.getFloatProperty("ship." + shipType + ".comps.prop." + propIndex + ".y");
					propIndex++;
				} else if (cmp.getClass().getAnnotation(ComponentKind.class).value() == ComponentType.LASERS) {
					compX = conf.getFloatProperty("ship." + shipType + ".comps.turret." + turretIndex + ".x");
					compY = conf.getFloatProperty("ship." + shipType + ".comps.turret." + turretIndex + ".y");
					turretIndex++;
				} else {
					compX = conf.getFloatProperty("ship." + shipType + ".comps.core." + coreIndex + ".x");
					compY = conf.getFloatProperty("ship." + shipType + ".comps.core." + coreIndex + ".y");
					coreIndex++;
				}
				if (compX != null && compY != null) {
					cmp.getPosInShip().set(compX, compY);
				} else {
					cmp.setPosInShip(null);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void onShipComponentsUpdated(@MObserves ShipComponentsUpdated shipComponentsUpdated) {
		computeMaxDamageDt(shipComponentsUpdated.getShip());
		computeMaxDefenseDt(shipComponentsUpdated.getShip());
	}

	public void onShipDeath(@MObserves ShipDeath shipDeath) {
		Ship ship = world.getPlayerShip();
		if (shipDeath.getShip().getPlayer().getPlayerType() == PlayerType.AI) {
			ship.setXp(ship.getXp() + 1);

			XpAwardedAnimation xpAwardedAnim = animationFactory.newInstance(XpAwardedAnimation.class);
			xpAwardedAnim.setPos(shipDeath.getShip().getPosition());
			xpAwardedAnim.setXpAmount(1);
			world.addAnimation(xpAwardedAnim);

			messageManager.addMessage(new Message(messages.getFormattedString("killing.xpAward", 1)), world.getTime());
		}
	}

}
