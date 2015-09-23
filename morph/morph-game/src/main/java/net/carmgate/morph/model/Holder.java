package net.carmgate.morph.model;

import javax.persistence.Embeddable;

@Embeddable
public class Holder<T extends Object> {

	private T value;

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

}
