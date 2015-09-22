package net.carmgate.morph.model.entities.components.prop;

import javax.persistence.Entity;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;
import net.carmgate.morph.model.entities.components.NeedsTarget;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;

@Entity
@NeedsTarget
@ComponentKind(ComponentType.PROPULSORS)
public class SimplePropulsor extends Component implements ForceSource {

	private final Vector2f force = new Vector2f();
	private final Vector2f tmpVect = new Vector2f();

	@Override
	public Vector2f getForce() {
		return force;
	}

}
