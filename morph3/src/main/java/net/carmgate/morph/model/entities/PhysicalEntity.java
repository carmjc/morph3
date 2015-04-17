package net.carmgate.morph.model.entities;

import java.util.Set;

import net.carmgate.morph.model.ForceSource;
import net.carmgate.morph.model.Vector2f;

public interface PhysicalEntity {

   Vector2f getPos();

   Vector2f getSpeed();

   float getMass();

   Set<ForceSource> getForceSources();
}
