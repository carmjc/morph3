package net.carmgate.morph.model.orders.ship;

import java.util.List;

import net.carmgate.morph.model.entities.physical.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;

// FIXME Refactor in move order
public class Flee extends Order implements ForceSource {

   private List<Ship> ships;
   private final Vector2f fleeSource = new Vector2f();
   private final Vector2f fleeDirection = new Vector2f();

   private Ship owner;

   @Override
   protected void evaluate() {
      // create barycenter of ships
      final int shipNumber = ships.size();
      fleeSource.copy(Vector2f.NULL);
      for (final Ship ship : ships) {
         fleeSource.add(ship.getPos());
      }
      fleeSource.scale(1f / shipNumber);

      fleeDirection.copy(Vector2f.NULL);
      fleeDirection.add(owner.getPos()).sub(fleeSource);

      setNextEvalTime(getNextEvalTime() + 100);
   }

   @Override
   public Vector2f getForce() {
      if (fleeDirection.length() == 0) {
         return fleeDirection.copy(0, 0);
      }
      return fleeDirection.scale(Ship.MAX_PROPULSOR_FORCE / fleeDirection.length());
   }

   public void setAttributes(Ship owner, List<Ship> ships) {
      this.owner = owner;
      this.ships = ships;
   }
}
