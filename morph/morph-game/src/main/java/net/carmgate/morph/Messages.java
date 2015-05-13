package net.carmgate.morph;

import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class Messages {

   private ResourceBundle messageBundle;

   @PostConstruct
   private void init() {
      messageBundle = ResourceBundle.getBundle("i18n.messages"); //$NON-NLS-1$
   }

   public final String getString(String key) {
      return messageBundle.getString(key);
   }
}
