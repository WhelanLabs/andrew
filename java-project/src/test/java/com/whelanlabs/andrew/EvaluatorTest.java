package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

public class EvaluatorTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void evaluate_oneThoght_getResult() {
      
      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Evaluator evaluator = new Evaluator(thought.getGoal());
      
      // TODO: create test range.
      TestRange testRange = null;
      
      evaluator.evaluateThoughts(testRange, 3);
      
      fail("test not complete.");
   }

}
