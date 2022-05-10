package com.whelanlabs.andrew;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;

public class GoalTest {

   private static Logger logger = LogManager.getLogger(GoalTest.class);
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void newGoal_validValues_created() {
      Goal goal = new Goal("next", Direction.outbound, 1, "value");
      
      assert(goal != null);
      assert("next".equals(goal.getRelationType()));
      assert(goal.getDirection() == Direction.outbound);
      assert(goal.getDistance() == 1);
      assert("value".equals(goal.getTargetProperty()));
   }

   @Test(expected = RuntimeException.class)
   public void newGoal_badDistance_exception() {
      new Goal("next", Direction.outbound, 0, "value");
      logger.debug("never to reach here...");
   }
   
   @Test(expected = RuntimeException.class)
   public void newGoal_badDirection_exception() {
      new Goal("next", Direction.any, 1, "value");
   }
   
}

