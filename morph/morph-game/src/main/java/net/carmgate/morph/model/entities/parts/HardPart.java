package net.carmgate.morph.model.entities.parts;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import net.carmgate.morph.model.entities.components.Component;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class HardPart<C extends Component> extends Part<C> {

}
