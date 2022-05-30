package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

public class EvaluatorTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "andrew_test_database";
      App.initialize(databaseName);
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }


   @Test
   public void evaluate_oneThoght_getResult() throws Exception {
      
      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Evaluator evaluator = new Evaluator(thought.getGoal());
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      // TODO: create test range.
      Integer maxTime = 500; // test data goes to time~=1000
      
      evaluator.evaluateThoughts(maxTime, 3);
      
      fail("test not complete.");
   }

}
