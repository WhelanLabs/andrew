package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;

public class GeneralScoringMachineTest {

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
   public void scoreAndRank_threeThoghts_getgetRelativeScores() throws Exception {

      Thought t1 = TestHelper.buildModifiedInitialTestThought();
      
      // TODO: add thought #2 - return starting node value
      Thought t2 = TestHelper.buildMultiplicationThought(t1.getGoal(), 1.0f);
      
      // TODO: add thought #3 - return starting node value X 1.04
      Thought t3 = TestHelper.buildMultiplicationThought(t1.getGoal(), 1.04f);
      
      Evaluator evaluator = new Evaluator(t1.getGoal());

      Integer maxTime = 500; // test data goes to time~=1000

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(20, maxTime, 10);
      // logger.debug("evualationResults: " + evualationResults);

      assert (evualationResults.size() == 30) : evualationResults.size();
      
      ScoringMachine scoringMachine = new GeneralScoringMachine();
      List<ThoughtScore> thoughtScores = scoringMachine.scoreAndRank(evualationResults);
      
      assert (thoughtScores.size() == 3) : thoughtScores;
      assert ("thoughtName".equals(thoughtScores.get(0).getThoughtName())) : thoughtScores;
      
      fail("not yet implemented");
   }

}
