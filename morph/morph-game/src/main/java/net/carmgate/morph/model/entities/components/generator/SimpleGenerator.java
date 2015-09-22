package net.carmgate.morph.model.entities.components.generator;

import javax.persistence.Entity;

import net.carmgate.morph.model.entities.components.Component;
import net.carmgate.morph.model.entities.components.ComponentKind;
import net.carmgate.morph.model.entities.components.ComponentType;

@Entity
@ComponentKind(ComponentType.GENERATORS)
public class SimpleGenerator extends Component {

}
