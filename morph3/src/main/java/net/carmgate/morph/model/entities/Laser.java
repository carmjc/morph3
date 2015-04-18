package net.carmgate.morph.model.entities;


public class Laser implements Animation {

	private final Ship target;
	private final Ship source;

	public Laser(Ship source, Ship target) {
		this.source = source;
		this.target = target;
	}

	public Ship getSource() {
		return source;
	}

	public Ship getTarget() {
		return target;
	}
}
