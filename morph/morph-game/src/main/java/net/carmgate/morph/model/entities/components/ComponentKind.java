package net.carmgate.morph.model.entities.components;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentKind {

   ComponentType value();
}
