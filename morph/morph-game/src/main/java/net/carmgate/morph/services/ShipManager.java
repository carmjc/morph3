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
import net.carmgate.morph.model.Player.PlayerType;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.ship.Ship;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;

@Singleton
public class ShipManager {

	@Inject private Conf conf;
	@Inject private World world;
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


	public void init(Ship ship) {
		ship.setCreationTime(world.getTime());

		computeMaxDamageDt(ship);
		computeMaxDefenseDt(ship);

		// conf
		ship.setXpMax(conf.getIntProperty("xp.max"));
		ship.setPerceptionRadius(conf.getFloatProperty("ship.perceptionRadius"));
	}

	@PostConstruct
	private void init() {
		eventManager.scanAndRegister(this);
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
			xpAwardedAnim.setPos(shipDeath.getShip().getPos());
			xpAwardedAnim.setXpAmount(1);
			world.getWorldAnimations().add(xpAwardedAnim);

			messageManager.addMessage(new Message(messages.getFormattedString("killing.xpAward", 1)), world.getTime());
		}
	}

}
