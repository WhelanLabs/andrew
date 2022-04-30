package com.whelanlabs.andrew;

public class AbsDistanceCriteria implements Criteria {

   @Override
   public Double calculateFitness(Number estimated, Number actual) {
      Double result = Math.abs(estimated.doubleValue() - actual.doubleValue());
      return result;
   }

}
