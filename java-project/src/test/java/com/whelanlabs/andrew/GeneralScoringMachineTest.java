package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeneralScoringMachineTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void scoreAndRank_threeThoghts_getgetRelativeScores() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      
      // TODO: add thought #2 - return starting node value
      
      // TODO: add thought #3 - return starting node value 1/2 time, null 1/2 time
      
      Evaluator evaluator = new Evaluator(thought.getGoal());

      Integer maxTime = 500; // test data goes to time~=1000

      List<Evaluation> evualationResults = evaluator.evaluateThoughts(20, maxTime, 10);
      // logger.debug("evualationResults: " + evualationResults);

      assert (evualationResults.size() == 30) : evualationResults;
      
      ScoringMachine scoringMachine = new GeneralScoringMachine();
      List<ThoughtScore> thoughtScores = scoringMachine.scoreAndRank(evualationResults);
      
      assert (thoughtScores.size() == 3) : thoughtScores;
      assert ("thoughtName".equals(thoughtScores.get(0).getThoughtName())) : thoughtScores;
      
      fail("not yet implemented");
   }

}
