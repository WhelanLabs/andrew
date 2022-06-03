package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;

public class AveragePercentageScoringMachineTest {

   private static Logger logger = LogManager.getLogger(AveragePercentageScoringMachineTest.class);
   
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
      Thought t3 = TestHelper.buildMultiplicationThought(t1.getGoal(), 1.1f);
      
      Evaluator evaluator = new Evaluator(t1.getGoal());

      Integer maxTime = 500; // test data goes to time~=1000

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(20, maxTime, 10);

      assert (evualationResults.size() == 30) : evualationResults.size();
      
      logger.debug("evualationResults = " + evualationResults );
      
      ScoringMachine scoringMachine = new AveragePercentageScoringMachine();
      List<ThoughtScore> thoughtScores = scoringMachine.scoreAndRank(evualationResults);

      assert (thoughtScores.size() == 3) : thoughtScores;
      assert (t1.getKey().equals(thoughtScores.get(0).getThoughtKey())) : thoughtScores;
      
      logger.debug("thoughtScores = " + thoughtScores );
   }

   @Test
   public void scoreAndRank_guessIsNull_scoreIsZero() throws Exception {

      List<Evaluation> evualationResults = new ArrayList<>();
      Thought thought = TestHelper.buildMultiplicationThought(1.0f);
      Evaluation nullEval = new Evaluation(thought.getThoughtNode(), null, 7);
      evualationResults.add(nullEval);
      
      ScoringMachine scoringMachine = new AveragePercentageScoringMachine();
      List<ThoughtScore> thoughtScores = scoringMachine.scoreAndRank(evualationResults);

      assert (thoughtScores.size() == 1) : thoughtScores;
      assert (0 == thoughtScores.get(0).getThoughtScore()) : thoughtScores;
      
      logger.debug("thoughtScores = " + thoughtScores );
   }
}
