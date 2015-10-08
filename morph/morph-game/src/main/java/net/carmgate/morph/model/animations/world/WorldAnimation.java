package net.carmgate.morph.model.animations.world;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jbox2d.common.Vec2;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.MWorld;
import net.carmgate.morph.model.animations.Animation;

public abstract class WorldAnimation extends Animation {

	@Inject private MWorld world;
	@Inject private Conf conf;

	private Vec2 pos;
	private long creationTime;

	public long getCreationTime() {
		return creationTime;
	}

	public Vec2 getPos() {
		return pos;
	}

	@PostConstruct
	private void init() {
		creationTime = world.getTime();
		setDuration(conf.getIntProperty(getClass().getCanonicalName() + ".duration"));
	}

	public void setPos(Vec2 pos) {
		this.pos = pos;
	}
}
