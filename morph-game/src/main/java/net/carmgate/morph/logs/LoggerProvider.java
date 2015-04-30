package net.carmgate.morph.logs;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProvider {

   @Produces
   private Logger produceLogger(InjectionPoint ip) {
      return LoggerFactory.getLogger(ip.getBean().getBeanClass());
   }
}
