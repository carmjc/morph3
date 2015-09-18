package net.carmgate.morph.ui.renderers;

import java.io.InputStream;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MorphFont extends AngelCodeFont {

	private int fontSize;

	public MorphFont(String fntFile, Image image) throws SlickException {
		super(fntFile, image);
	}

	public MorphFont(String fntFile, Image image, boolean caching) throws SlickException {
		super(fntFile, image, caching);
		// TODO Auto-generated constructor stub
	}

	public MorphFont(String name, InputStream fntFile, InputStream imgFile) throws SlickException {
		super(name, fntFile, imgFile);
		// TODO Auto-generated constructor stub
	}

	public MorphFont(String name, InputStream fntFile, InputStream imgFile, boolean caching) throws SlickException {
		super(name, fntFile, imgFile, caching);
		// TODO Auto-generated constructor stub
	}

	public MorphFont(String fntFile, String imgFile) throws SlickException {
		super(fntFile, imgFile);
		// TODO Auto-generated constructor stub
	}

	public MorphFont(String fntFile, String imgFile, boolean caching) throws SlickException {
		super(fntFile, imgFile, caching);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getHeight(CharSequence text) {
		return (int) ((float) super.getHeight(text) / getLineHeight() * fontSize);
	}

	public int getTargetFontSize() {
		return fontSize;
	}

	@Override
	public int getWidth(CharSequence text) {
		return (int) ((float) super.getWidth(text) / getLineHeight() * fontSize);
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
