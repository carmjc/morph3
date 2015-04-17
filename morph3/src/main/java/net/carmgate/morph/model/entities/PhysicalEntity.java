package net.carmgate.morph.model.entities;

import java.util.Set;

import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.physics.ForceSource;

public interface PhysicalEntity {

   Vector2f getPos();

   Vector2f getSpeed();

   float getMass();

   Set<ForceSource> getForceSources();
}
