package net.carmgate.morph.model.api;

import net.carmgate.morph.model.events.WorldChanged;


public interface WorldChangeListener {

   void onWorldChanged(WorldChanged event);
}
