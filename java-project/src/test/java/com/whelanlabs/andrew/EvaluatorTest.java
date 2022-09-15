package com.whelanlabs.andrew;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.andrew.process.evaluate.Evaluation;
import com.whelanlabs.andrew.process.evaluate.Evaluator;

public class EvaluatorTest {

   private static Logger logger = LogManager.getLogger(EvaluatorTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "andrew_test_database";
      App.initialize(databaseName);
      App.loadDatasetToDataGraph(new LinearDataset());
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }
   
   
   @Test
   public void evaluate_oneThoght_getResult() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Evaluator evaluator = new Evaluator(thought.getGoalNode());

      Long maxTime = 500l; // test data goes to time~=1000

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(20l, maxTime, 3);
      logger.debug("evualationResults: " + evualationResults);

      assert (evualationResults.size() == 3) : evualationResults;
   }
   
   @Test
   public void evaluate_thoughtProcessGoesOutOfBounds_getNullGuess() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Evaluator evaluator = new Evaluator(thought.getGoalNode());

      App.loadDatasetToDataGraph(new LinearDataset());

      Long maxTime = 9l; // the thought goes back back in time 10 places - this will go into pre-history

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(7l, maxTime, 1);
      logger.debug("evualationResults: " + evualationResults);

      assert (evualationResults.size() == 1) : evualationResults;
      assert (null==evualationResults.get(0).getGuess()) : evualationResults;
   }
   
   @Test
   public void evaluate_thoughtProcessNoStartingNode_getNull() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Evaluator evaluator = new Evaluator(thought.getGoalNode());

      App.loadDatasetToDataGraph(new LinearDataset());

      Long maxTime = 1l; // the thought goes back back in time 10 places - this will go into pre-history

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(0l, maxTime, 1);
      logger.debug("evualationResults: " + evualationResults);

      assert (evualationResults.size() == 0) : evualationResults;
   }
   
}
