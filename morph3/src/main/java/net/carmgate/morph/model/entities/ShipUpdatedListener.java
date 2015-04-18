package net.carmgate.morph.model.entities;

import net.carmgate.morph.model.events.ShipUpdated;

public interface ShipUpdatedListener {

	void onShipUpdated(ShipUpdated event);
}
