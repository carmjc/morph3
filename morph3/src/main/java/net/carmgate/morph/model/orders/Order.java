package net.carmgate.morph.model.orders;

import net.carmgate.morph.model.World;

public abstract class Order {

	private World world;
	private long nextEvalTime = 0;

	public Order() {
	}

	public void eval() {
		if (world.getTime() > nextEvalTime) {
			evaluate();
		}
	}

	protected abstract void evaluate();

	public long getNextEvalTime() {
		return nextEvalTime;
	}

	public World getWorld() {
		return world;
	}

	protected void setNextEvalTime(long nextEvalTime) {
		this.nextEvalTime = nextEvalTime;
	}

	public void setWorld(World world) {
		this.world = world;
		nextEvalTime = world.getTime();
	}

}
