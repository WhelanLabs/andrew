package com.whelanlabs.andrew;

import com.whelanlabs.kgraph.engine.Node;

public class Evaluator {

   public Double evualate(Node guess, Node answer, Goal goal, Criteria criteria) {
      Number estimated = (Number)guess.getAttribute(goal.getTargetProperty());
      Number actual = (Number)answer.getAttribute(goal.getTargetProperty());

      Double result = criteria.calculateFitness(estimated, actual);
      return result;
   }
}
