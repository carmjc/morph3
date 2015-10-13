package net.carmgate.morph.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.carmgate.morph.conf.Conf;

public class Window {
	@Inject private Conf conf;

	private int width;
	private int height;

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	@PostConstruct
	void init() {
//		width = conf.getIntProperty("window.initialWidth"); //$NON-NLS-1$
//		height = conf.getIntProperty("window.initialHeight"); //$NON-NLS-1$
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
