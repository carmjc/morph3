package net.carmgate.morph.model.entities.components.repair;

import javax.persistence.Entity;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;

@Entity
@ComponentKind(ComponentType.REPAIRER)
public class SimpleRepairer extends Component {

}
