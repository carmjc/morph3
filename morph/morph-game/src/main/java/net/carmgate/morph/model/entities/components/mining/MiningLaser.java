package net.carmgate.morph.model.entities.components.mining;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import net.carmgate.morph.events.world.entities.component.ComponentLoaded;
import net.carmgate.morph.model.animations.ship.MiningLaserAnim;
import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;

@Entity
@NeedsTarget
@ComponentKind(ComponentType.MINING_LASERS)
public class MiningLaser extends Component {

	@Transient private MiningLaserAnim laserAnim;

	@Override
	@PostConstruct
	@PostLoad
	protected void postLoad() {
		super.postLoad();
		eventManager.addEvent(new ComponentLoaded(this));
	}
}
