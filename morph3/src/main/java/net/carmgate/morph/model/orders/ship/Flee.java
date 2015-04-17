package net.carmgate.morph.model.orders.ship;

import java.util.List;

import net.carmgate.morph.model.entities.Ship;
import net.carmgate.morph.model.geometry.Vector2f;
import net.carmgate.morph.model.orders.Order;
import net.carmgate.morph.model.physics.ForceSource;

public class Flee extends Order implements ForceSource {

   private static final float MAX_FLEE_FORCE = 0.01f;

   private List<Ship> ships;
   private Vector2f fleeSource = new Vector2f();
   private Vector2f fleeDirection = new Vector2f();

   private Ship owner;

   public Flee(Ship owner, List<Ship> ships) {
      this.owner = owner;
      this.ships = ships;
   }

   @Override
   public void evaluate(long nextEvaluationInMillis) {
      // create barycenter of ships
      int shipNumber = ships.size();
      fleeSource.copy(Vector2f.NULL);
      for (Ship ship : ships) {
         fleeSource.add(ship.getPos());
      }
      fleeSource.scale(1f / shipNumber);

      fleeDirection.copy(Vector2f.NULL);
      fleeDirection.add(owner.getPos()).sub(fleeSource);
   }

   @Override
   public Vector2f getForce() {
      if (fleeDirection.length() == 0) {
         return fleeDirection.copy(0, 0);
      }
      return fleeDirection.scale(MAX_FLEE_FORCE / owner.getMass() / fleeDirection.length());
   }
}
