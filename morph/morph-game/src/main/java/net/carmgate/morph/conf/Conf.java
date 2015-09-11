package net.carmgate.morph.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class Conf {

	@Inject
	private Logger LOGGER;

	private final Properties prop;

	private Conf() {
		prop = new Properties();
		try (InputStream in = getClass().getResourceAsStream("/config.properties")) { //$NON-NLS-1$
			prop.load(in);
		} catch (final IOException e) {
			LOGGER.error("Exception raised loading properties", e); //$NON-NLS-1$
		}
	}

	public String[] getArrayProperty(String key) {
		String property = getProperty(key);
		String[] strValue = property!= null ? property.split(";") : null;
		return strValue;
	}

	public Byte getCharProperty(String key) {
		String property = prop.getProperty(key);
		if (property == null) {
			return null;
		}
		return property.getBytes()[0];
	}

	public float[] getFloatArrayProperty(String key) {
		String[] strValue = getArrayProperty(key);
		float[] value = null;
		if (strValue != null) {
			value = new float[strValue.length];
			for (int i = 0; i < value.length; i++) {
				value[i] = Float.parseFloat(strValue[i]);
			}
		}
		return value;
	}

	public Float getFloatProperty(String key) {
		String property = prop.getProperty(key);
		if (property == null) {
			return null;
		}
		return Float.parseFloat(property);
	}

	public Integer getIntProperty(String key) {
		String property = prop.getProperty(key);
		if (property == null) {
			return null;
		}
		return Integer.parseInt(property);
	}

	public String getProperty(String key) {
		return prop.getProperty(key);
	}
}
