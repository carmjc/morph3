package net.carmgate.morph.model;

import java.util.List;

import net.carmgate.morph.model.entities.Ship;

public class Flee extends Goal implements ForceSource {

   private static final float MAX_FLEE_FORCE = 0.01f;

   private List<Ship> ships;
   private Vector2f fleeSource = new Vector2f();
   private Vector2f fleeDirection = new Vector2f();

   private Ship owner;

   public Flee(Ship owner, List<Ship> ships) {
      super(0);
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
