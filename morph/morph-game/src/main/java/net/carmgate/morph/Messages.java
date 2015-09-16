package net.carmgate.morph;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class Messages {

	private ResourceBundle messageBundle;

	public final String getFormattedString(String key, Object... objects) {
		return MessageFormat.format(getString(key), objects);
	}

	public final String getString(String key) {
		return messageBundle.getString(key);
	}

	@PostConstruct
	private void init() {
		messageBundle = ResourceBundle.getBundle("i18n.messages"); //$NON-NLS-1$
	}
}
