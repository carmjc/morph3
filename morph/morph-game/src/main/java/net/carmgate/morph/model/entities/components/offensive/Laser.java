package net.carmgate.morph.model.entities.components.offensive;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import net.carmgate.morph.events.entities.component.ComponentLoaded;
import net.carmgate.morph.events.entities.ship.ShipDeath;
import net.carmgate.morph.events.mgt.MObserves;
import net.carmgate.morph.model.animations.ship.LaserAnim;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.entities.parts.hardParts.OverClocking;

@Entity
@NeedsTarget
@ComponentKind(ComponentType.LASERS)
public class Laser extends Component {

	@Transient private LaserAnim laserAnim;
	@Transient private OverClocking overClocking; // test code. This should be persisted

	@SuppressWarnings("unused")
	private void onShipDeath(@MObserves ShipDeath shipDeath) {
		if (getTarget() == shipDeath.getShip()) {
			setTarget(null);
		}
	}

	@Override
	@PostLoad
	@PostConstruct
	protected void postLoad() {
		super.postLoad();
		eventManager.addEvent(new ComponentLoaded(this));
	}

}
