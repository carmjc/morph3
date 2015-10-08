package net.carmgate.morph.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.newdawn.slick.util.Log;

public class AngelCodeFont {
	public static class Glyph {
		public final short id;
		public final short x;
		public final short y;
		public final short width;
		public final short height;
		public final short xoffset;
		public final short yoffset;
		public final short xadvance;
		protected short dlIndex;

		protected short[] kerning;
		protected Glyph(short id, short x, short y, short width, short height, short xoffset, short yoffset, short xadvance) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
			this.xadvance = xadvance;
		}

		public int getKerning(int otherCodePoint) {
			if (kerning == null) {
				return 0;
			}
			int low = 0;
			int high = kerning.length - 1;
			while (low <= high) {
				int midIndex = low + high >>> 1;
				int value = kerning[midIndex];
				int foundCodePoint = value & 0xFF;
				if (foundCodePoint < otherCodePoint) {
					low = midIndex + 1;
				} else if (foundCodePoint > otherCodePoint) {
					high = midIndex - 1;
				} else {
					return value >> 8;
				}
			}
			return 0;
		}

		@Override
		public String toString() {
			return "[CharDef id=" + id + " x=" + x + " y=" + y + "]";
		}
	}

	private Glyph[] chars;
	private int lineHeight;
	private boolean singleCase = false;
	private short ascent;
	private short descent;
	private short leading;
	private short scaleW;
	private short scaleH;
	private short size;
	private short outline;

	private String imgFile;

	public AngelCodeFont(String fntFile, String imgFile) {
		this.imgFile = imgFile;
		parseFnt(ClassLoader.getSystemResourceAsStream(fntFile));
	}

	public int getAscent() {
		return ascent;
	}

	public int getDescent() {
		return descent;
	}
	public Glyph getGlyph(char c) {
		Glyph g = c < 0 || c >= chars.length ? null : chars[c];
		if (g != null) {
			return g;
		}
		if (g == null && singleCase) {
			if (c >= 'A' && c <= 'Z') {
				c = (char) (c + ' ');
			} else if (c >= 'a' && c <= 'z') {
				c = (char) (c - ' ');
			}
		}
		return c < 0 || c >= chars.length ? null : chars[c];
	}

	// public AngelCodeFont(String fntFile)
	// throws SlickException {
	// parseFnt(getClass().getResourceAsStream(fntFile));
	// }
	//
	// public AngelCodeFont(String fntFile, boolean caching)
	// throws SlickException {
	// displayListCaching = caching;
	// parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	// }

	// public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile)
	// throws SlickException {
	// parseFnt(fntFile);
	// }

	// public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile, boolean caching)
	// throws SlickException {
	// displayListCaching = caching;
	// parseFnt(fntFile);
	// }

	public int getHeight(CharSequence text) {
		// DisplayList displayList = null;
		// if (displayListCaching) {
		// displayList = (DisplayList) displayLists.get(text);
		// if (displayList != null && displayList.height != null) {
		// return displayList.height.intValue();
		// }
		// }

		int lines = 0;
		int maxHeight = 0;
		for (int i = 0; i < text.length(); ++i) {
			char id = text.charAt(i);
			if (id == '\n') {
				++lines;
				maxHeight = 0;
			} else {
				if (id == ' ') {
					continue;
				}
				Glyph charDef = getGlyph(id);
				if (charDef == null) {
					continue;
				}

				maxHeight = Math.max(charDef.height + charDef.yoffset, maxHeight);
			}
		}

		maxHeight += lines * getLineHeight();

		// if (displayList != null) {
		// displayList.height = new Short((short) maxHeight);
		// }

		return maxHeight;
	}

	public String getImgFile() {
		return imgFile;
	}

	// public AngelCodeFont(String fntFile, String imgFile, boolean caching)
	// throws SlickException {
	// displayListCaching = caching;
	// parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	// }

	// public void drawString(float x, float y, CharSequence text) {
	// drawString(x, y, text, Color.white);
	// }

	// public void drawString(float x, float y, CharSequence text, Color col) {
	// drawString(x, y, text, col, 0, text.length() - 1);
	// }

	// public void drawString(float x, float y, CharSequence text, Color col, int startIndex, int endIndex) {
	// fontImage.bind();
	// col.bind();
	//
	// GL.glTranslatef(x, y, 0.0F);
	// if (displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
	// DisplayList displayList = (DisplayList) displayLists.get(text);
	// if (displayList != null) {
	// GL.glCallList(displayList.id);
	// } else {
	// displayList = new DisplayList(null);
	// displayList.text = text;
	// int displayListCount = displayLists.size();
	// if (displayListCount < 200) {
	// displayList.id = baseDisplayListID + displayListCount;
	// } else {
	// displayList.id = eldestDisplayListID;
	// displayLists.remove(eldestDisplayList.text);
	// }
	//
	// displayLists.put(text, displayList);
	//
	// GL.glNewList(displayList.id, 4865);
	// render(text, startIndex, endIndex);
	// GL.glEndList();
	// }
	// } else {
	// render(text, startIndex, endIndex);
	// }
	// GL.glTranslatef(-x, -y, 0.0F);
	// }

	public int getLineHeight() {
		return lineHeight;
	}

	public short getOutline() {
		return outline;
	}

	public short getScaleH() {
		return scaleH;
	}

	public short getScaleW() {
		return scaleW;
	}

	// public Image getImage() {
	// return fontImage;
	// }

	public short getSize() {
		return size;
	}

	public int getWidth(CharSequence text) {
		// DisplayList displayList = null;
		// if (displayListCaching) {
		// displayList = (DisplayList) displayLists.get(text);
		// if (displayList != null && displayList.width != null) {
		// return displayList.width.intValue();
		// }
		// }

		int maxWidth = 0;
		int width = 0;
		Glyph lastCharDef = null;
		int i = 0;
		for (int n = text.length(); i < n; ++i) {
			char id = text.charAt(i);
			if (id == '\n') {
				width = 0;
			} else {
				Glyph charDef = getGlyph(id);
				if (charDef == null) {
					continue;
				}

				if (lastCharDef != null) {
					width += lastCharDef.getKerning(id);
				}

				lastCharDef = charDef;

				if (i < n - 1 || charDef.width == 0) {
					width += charDef.xadvance;
				} else {
					width += charDef.width + charDef.xoffset;
				}
				maxWidth = Math.max(maxWidth, width);
			}
		}
		// if (displayList != null) {
		// displayList.width = new Short((short) maxWidth);
		// }

		return maxWidth;
	}

	public int getYOffset(String text) {
		// DisplayList displayList = null;
		// if (displayListCaching) {
		// displayList = (DisplayList) displayLists.get(text);
		// if (displayList != null && displayList.yOffset != null) {
		// return displayList.yOffset.intValue();
		// }
		// }

		int stopIndex = text.indexOf(10);
		if (stopIndex == -1) {
			stopIndex = text.length();
		}

		int minYOffset = 10000;
		for (int i = 0; i < stopIndex; ++i) {
			Glyph charDef = getGlyph(text.charAt(i));
			if (charDef == null) {
				continue;
			}
			minYOffset = Math.min(charDef.yoffset, minYOffset);
		}

		// if (displayList != null) {
		// displayList.yOffset = new Short((short) minYOffset);
		// }

		return minYOffset;
	}

	public boolean isSingleCase() {
		return singleCase;
	}

	private Glyph parseChar(String line) {
		StringTokenizer tokens = new StringTokenizer(line, " =");

		tokens.nextToken();
		tokens.nextToken();
		short id = Short.parseShort(tokens.nextToken());
		if (id < 0) {
			return null;
		}
		if (id > 255) {
			throw new IllegalStateException("Invalid character '" + id + "': SpriteFont does not support characters above " + 255);
		}

		tokens.nextToken();
		short x = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short y = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short width = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short height = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short xoffset = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short yoffset = Short.parseShort(tokens.nextToken());
		tokens.nextToken();
		short xadvance = Short.parseShort(tokens.nextToken());

		if (id != 32) {
			lineHeight = Math.max(height + yoffset, lineHeight);
		}
		// Image img = fontImage.getSubImage(x, y, width, height);
		return new Glyph(id, x, y, width, height, xoffset, yoffset, xadvance);
	}

	private void parseFnt(InputStream fntFile) {
		// if (displayListCaching) {
		// baseDisplayListID = GL.glGenLists(200);
		// if (baseDisplayListID == 0) {
		// displayListCaching = false;
		// }
		// }

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(fntFile));

			String info = in.readLine();
			size = parseMetric(info, "size=");
			outline = parseMetric(info, "outline=");

			String common = in.readLine();
			ascent = parseMetric(common, "base=");
			lineHeight = parseMetric(common, "lineHeight=");

			descent = parseMetric(common, "descent=");
			leading = parseMetric(common, "leading=");

			scaleW = parseMetric(common, "scaleW=");
			scaleH = parseMetric(common, "scaleH=");

			String page = in.readLine();

			Map<Short, List<Short>> kerning = new HashMap<>(64);
			List<Glyph> charDefs = new ArrayList<>(255);
			int maxChar = 0;
			boolean done = false;
			while (!done) {
				String line = in.readLine();
				if (line == null) {
					done = true;
				} else {
					if (!line.startsWith("chars c")) {
						if (line.startsWith("char")) {
							Glyph def = parseChar(line);
							if (def != null) {
								maxChar = Math.max(maxChar, def.id);
								charDefs.add(def);
							}
						}
					}
					if (!line.startsWith("kernings c")) {
						if (line.startsWith("kerning")) {
							StringTokenizer tokens = new StringTokenizer(line, " =");
							tokens.nextToken();
							tokens.nextToken();
							short first = Short.parseShort(tokens.nextToken());

							tokens.nextToken();
							int second = Integer.parseInt(tokens.nextToken());

							tokens.nextToken();
							int offset = Integer.parseInt(tokens.nextToken());

							List<Short> values = kerning.get(Short.valueOf(first));
							if (values == null) {
								values = new ArrayList<>();
								kerning.put(Short.valueOf(first), values);
							}

							values.add(Short.valueOf((short) (offset << 8 | second)));
						}
					}
				}
			}
			chars = new Glyph[maxChar + 1];
			for (Glyph def : charDefs) {
				chars[def.id] = def;
			}

			for (Map.Entry<Short, List<Short>> entry : kerning.entrySet()) {
				short first = entry.getKey().shortValue();
				List<Short> valueList = entry.getValue();
				short[] valueArray = new short[valueList.size()];
				for (int i = 0; i < valueList.size(); ++i) {
					valueArray[i] = valueList.get(i).shortValue();
				}
				chars[first].kerning = valueArray;
			}
		} catch (IOException e) {
			Log.error(e);
			throw new IllegalStateException("Failed to parse font file: " + fntFile);
		}
	}

	private short parseMetric(String str, String sub) {
		int ind = str.indexOf(sub);
		if (ind != -1) {
			String subStr = str.substring(ind + sub.length());
			ind = subStr.indexOf(32);
			return Short.parseShort(subStr.substring(0, ind != -1 ? ind : subStr.length()));
		}
		return -1;
	}

	private void render(CharSequence text, int start, int end) {
		// GL.glBegin(7);

		int x = 0;
		int y = 0;
		Glyph lastCharDef = null;

		for (int i = 0; i < text.length(); ++i) {
			char id = text.charAt(i);
			if (id == '\n') {
				x = 0;
				y += getLineHeight();
			} else {
				Glyph charDef = getGlyph(id);
				if (charDef == null) {
					continue;
				}
				if (lastCharDef != null) {
					x += lastCharDef.getKerning(id);
				} else {
					x -= charDef.xoffset;
				}
				lastCharDef = charDef;

				if (i >= start && i <= end) {
					// charDef.image.drawEmbedded(x + charDef.xoffset, y + charDef.yoffset, charDef.width, charDef.height);
				}

				x += charDef.xadvance;
			}
		}
		// GL.glEnd();
	}

	public void setSingleCase(boolean enabled) {
		singleCase = enabled;
	}

}
