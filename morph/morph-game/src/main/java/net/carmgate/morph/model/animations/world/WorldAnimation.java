package net.carmgate.morph.model.animations.world;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;
import net.carmgate.morph.model.World;
import net.carmgate.morph.model.animations.Animation;
import net.carmgate.morph.model.geometry.Vector2f;

public abstract class WorldAnimation extends Animation {

	@Inject private World world;
	@Inject private Conf conf;

	private Vector2f pos;
	private long creationTime;
	private int duration;

	public long getCreationTime() {
		return creationTime;
	}

	public int getDuration() {
		return duration;
	}

	public Vector2f getPos() {
		return pos;
	}

	@PostConstruct
	private void init() {
		creationTime = world.getTime();
		duration = conf.getIntProperty(getClass().getCanonicalName() + ".duration");
	}

	public void setPos(Vector2f pos) {
		this.pos = pos;
	}
}
