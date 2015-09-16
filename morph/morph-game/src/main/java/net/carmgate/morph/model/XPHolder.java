package net.carmgate.morph.model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import net.carmgate.morph.Messages;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.Player.PlayerType;
import net.carmgate.morph.model.animations.AnimationFactory;
import net.carmgate.morph.model.animations.AnimationType;
import net.carmgate.morph.model.animations.world.XpAwardedAnimation;
import net.carmgate.morph.model.entities.physical.ship.Ship;
import net.carmgate.morph.ui.MessageManager;
import net.carmgate.morph.ui.MessageManager.Message;

public class XPHolder {

	@Inject private World world;
	@Inject private AnimationFactory animationFactory;
	@Inject private Logger LOGGER;
	@Inject private MEventManager eventManager;
	@Inject private MessageManager messageManager;
	@Inject private Messages messages;

	private Ship ship;

	public Ship getShip() {
		return ship;
	}

	@PostConstruct
	private void init() {
		eventManager.scanAndRegister(this);
	}

	public void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (shipDeath.getShip().getPlayer().getPlayerType() == PlayerType.AI && ship.getPlayer().getPlayerType() == PlayerType.PLAYER) {
			ship.setXp(ship.getXp() + 1);

			XpAwardedAnimation xpAwardedAnim = animationFactory.newInstance(AnimationType.XP_AWARDED);
			xpAwardedAnim.setPos(shipDeath.getShip().getPos());
			xpAwardedAnim.setXpAmount(1);
			world.getWorldAnimations().add(xpAwardedAnim);

			messageManager.addMessage(new Message(messages.getFormattedString("killing.xpAward", 1)));
		}
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}
}
