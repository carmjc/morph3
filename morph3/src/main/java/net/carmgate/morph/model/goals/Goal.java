package net.carmgate.morph.model.goals;

public abstract class Goal {

   private int priority;
   private Goal nextGoal;

   protected Goal(int priority) {
      this.priority = priority;
   }

   public abstract void evaluate(long nextEvaluationInMillis);

   public int getPriority() {
      return priority;
   }

   public Goal getNextGoal() {
      return nextGoal;
   }

   public void setNextGoal(Goal nextGoal) {
      this.nextGoal = nextGoal;
   }

}
