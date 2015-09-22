package net.carmgate.morph.model.entities.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.events.mgt.MEventManager;
import net.carmgate.morph.model.entities.components.Component;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Part<C extends Component> {

	// Not available if it comes from the database
	@Transient @Inject private Conf conf;

	@Id private int id;
	private int level = 0;
	@ManyToOne
	private Component component;
	private Integer xpNeededForNextLevel;

	@Transient protected MEventManager eventManager;

	public Part() {
		super();
	}

	public abstract void computeEffectOnComponent(C cmp);

	public Component getComponent() {
		return component;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public int getXpNeededForNextLevel() {
		return xpNeededForNextLevel;
	}

	@PostConstruct
	protected void postConstruct() {
		xpNeededForNextLevel = conf.getIntProperty(getClass().getCanonicalName() + ".levelingXp");
		if (xpNeededForNextLevel == null) {
			xpNeededForNextLevel = 0;
		}
		postLoad();
	}

	@PostLoad
	private void postLoad() {
		eventManager = MEventManager.getInstance();
		eventManager.scanAndRegister(this);
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}