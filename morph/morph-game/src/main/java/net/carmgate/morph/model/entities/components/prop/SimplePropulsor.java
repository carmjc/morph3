package net.carmgate.morph.model.entities.components.prop;

import javax.persistence.Entity;

import org.jbox2d.common.Vec2;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.physics.ForceSource;

@Entity
@NeedsTarget
@ComponentKind(ComponentType.PROPULSORS)
public class SimplePropulsor extends Component implements ForceSource {

	private final Vec2 force = new Vec2();
	private final Vec2 tmpVect = new Vec2();

	@Override
	public Vec2 getForce() {
		return force;
	}

}
