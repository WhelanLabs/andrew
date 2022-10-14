package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.andrew.process.evaluate.Evaluation;
import com.whelanlabs.andrew.process.evaluate.Evaluator;
import com.whelanlabs.andrew.process.selection.AveragePercentageScoringMachine;
import com.whelanlabs.andrew.process.selection.ScoringMachine;
import com.whelanlabs.andrew.process.selection.ThoughtScore;

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
   public void scoreAndRank_guessIsNull_scoreIsZero() throws Exception {

      List<Evaluation> evualationResults = new ArrayList<>();
      Thought thought = TestHelper.buildMultiplicationThought(1.0f);
      Evaluation nullEval = new Evaluation(thought.getThoughtNode(), null, 7, null);
      evualationResults.add(nullEval);
      
      ScoringMachine scoringMachine = new AveragePercentageScoringMachine();
      List<ThoughtScore> thoughtScores = scoringMachine.scoreAndRank(evualationResults);

      assert (thoughtScores.size() == 1) : thoughtScores;
      assert (0 == thoughtScores.get(0).getThoughtScore()) : thoughtScores;
      
      logger.debug("thoughtScores = " + thoughtScores );
   }
}
