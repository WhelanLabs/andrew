package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

public class ThoughtTest {

   private static Logger logger = LogManager.getLogger(ThoughtTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "andrew_test_database";
      App.initialize(databaseName);
      App.getGardenGraph().flush();
      App.getDataGraph().flush();
      App.loadDatasetToDataGraph(new LinearDataset());
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

//   @Test
//   public void buildThought_valid_success() throws Exception {
//
//      App.getGardenGraph().flush();
//
//      Long pre_thought_count = App.getGardenGraph().getCount("thought");
//      Long pre_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
//      Long pre_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
//      Long pre_thought_result_count = App.getGardenGraph().getCount("thought_result");
//
//      Long pre_count = pre_thought_count + pre_thought_operation_count + pre_thought_sequence_count + pre_thought_result_count;
//
//      TestHelper.buildInitialTestThought();
//
//      Long post_thought_count = App.getGardenGraph().getCount("thought");
//      Long post_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
//      Long post_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
//      Long post_thought_result_count = App.getGardenGraph().getCount("thought_result");
//
//      Long post_count = post_thought_count + post_thought_operation_count + post_thought_sequence_count + post_thought_result_count;
//
//      assert (pre_count + 14 == post_count) : "{" + pre_count + ", " + post_count + "}";
//   }

   @Test
   public void runThought_valid_success() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThought();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> result = thought.forecast(startingNode);

      assert (null != result);
      logger.debug("result = " + result);

      Number guess = (Number) result.get("RESULT.output");
      // logger.debug("guess = " + guess);

      Node answerNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_510", "LinearDatasetNode");
      Number answer = (Number) answerNode.getAttribute("value");
      // logger.debug("answer = " + answer);

      assert (Math.abs(guess.floatValue() - answer.floatValue()) < 1) : "{" + guess + ", " + answer + "}";

   }

   @Test
   public void runThought_multiplyThought_success() throws Exception {

      Float multiplier = 1.04f;
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();

      Thought thought = TestHelper.buildMultiplicationThought(multiplier);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> result = thought.forecast(startingNode);

      assert (null != result);
      logger.debug("result = " + result);

      Number guess = (Number) result.get("RESULT.output");
      logger.debug("guess = " + guess);

      // Node answerNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_510",
      // "LinearDatasetNode");
      Number initialValue = (Number) startingNode.getAttribute("value");
      logger.debug("initialValue = " + initialValue);

      assert (Math.abs(guess.floatValue() - (initialValue.floatValue() * multiplier)) < 0.5) : "{" + guess + ", " + initialValue + "}";

   }

   @Test(expected = RuntimeException.class)
   public void runThought_invalidNodeType_exception() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithBadNode();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }

   @Test(expected = RuntimeException.class)
   public void runThought_noEnd_exception() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }

   @Test
   public void getOperationsByMaxLayer_initialTestThought_success() throws Exception {

      // Thought thought = TestHelper.buildInitialTestThought();
      
      Thought thought = TestHelper.buildModifiedInitialTestThought();
      
      List<Set<Node>> opsByLayer = thought.getOperationsByMaxLayer();

      assert (null != opsByLayer);
      assert (5 == opsByLayer.size()) : opsByLayer;

      Set<Node> layerTwo = opsByLayer.get(2);
      assert (1 == layerTwo.size()) : "size = " + layerTwo.size() + ", contents = " + layerTwo;
   }

   @Test
   public void clone_goodThought_getClone() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Thought clonedThought = thought.clone();
      
      assert (null != thought);
      assert (null != clonedThought);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      
      Map<String, Object> origResult = thought.forecast(startingNode);
      Map<String, Object> cloneResult = clonedThought.forecast(startingNode);
      Number origGuess = (Number) origResult.get("RESULT.output");
      Number cloneGuess = (Number) cloneResult.get("RESULT.output");
      
      assert (Math.abs(origGuess.floatValue() - cloneGuess.floatValue()) < .01): origGuess + ", " + cloneGuess;
   }
   
   @Test(expected = RuntimeException.class)
   public void clone_noEnd_getException() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();
      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();
      Thought clonedThought = thought.clone();
   }
   
   @Test(expected = RuntimeException.class)
   public void clone_noResult_getException() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();
      String thoughtKey = ElementHelper.generateKey();
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("thought_key", n1.getKey());
      e0.addAttribute("name", "e0" );
      App.getGardenGraph().upsert(goalNode, n1, e0);
      
      Thought thought = new Thought(n1);
      Thought clonedThought = thought.clone();
   }
   
   @Test(expected = RuntimeException.class)
   public void clone_noApproach_getException() throws Exception {
      //App.getDataGraph().flush();
      //App.getGardenGraph().flush();
      String thoughtKey = ElementHelper.generateKey();
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("thought_key", n1.getKey());
      e0.addAttribute("name", "e0" );
      App.getGardenGraph().upsert(goalNode, n1, e0);
      
      Thought thought = new Thought(n1);
      
      e0.addAttribute("thought_key", "bad");
      App.getGardenGraph().upsert(e0);
            
      Thought clonedThought = thought.clone();
   }
   
   @Test
   public void mutate_goodStartingThought_resultImpacted() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Thought clonedThought = thought.clone();
      
      assert (null != thought);
      assert (null != clonedThought);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      
      Map<String, Object> origResult = thought.forecast(startingNode);
      Map<String, Object> cloneResult = clonedThought.forecast(startingNode);
      Number origGuess = (Number) origResult.get("RESULT.output");
      Number cloneGuess = (Number) cloneResult.get("RESULT.output");
      
      assert (Math.abs(origGuess.floatValue() - cloneGuess.floatValue()) < .01): origGuess + ", " + cloneGuess;
      
      Thought mutatedClonedThought = clonedThought.mutate(2);
      
      Map<String, Object> mutatedCloneResult = mutatedClonedThought.forecast(startingNode);
      Number mutatedCloneGuess = (Number) mutatedCloneResult.get("RESULT.output");
      
      /* 
       * Note: If the following assertion fails, run it again.  There is a small chance 
       * that the mutation is so small as to not be detected.
       */
      assert (Math.abs(origGuess.floatValue() - mutatedCloneGuess.floatValue()) > .00001): origGuess + ", " + cloneGuess;
      
      logger.debug("origGuess = "+ origGuess);
      logger.debug("mutatedCloneGuess = "+ mutatedCloneGuess);
   }
   
   @Test
   public void merge_twoThoughts_combinedThought() throws Exception {

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
            
      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);
      
      Merger merger = new SimpleMerger();
      Thought childThought = merger.merge(thought1, thought2);

      Map<String, Object> t1Result = thought1.forecast(startingNode);
      Map<String, Object> t2Result = thought2.forecast(startingNode);
      
      logger.debug("### Start: Forcasting the Child Thought ###");
      Map<String, Object> t3Result = childThought.forecast(startingNode);
      Number t1Guess = (Number) t1Result.get("RESULT.output");
      Number t2Guess = (Number) t2Result.get("RESULT.output");
      Number t3Guess = (Number) t3Result.get("RESULT.output");
      
      assert (Math.abs(t1Guess.floatValue() - t2Guess.floatValue()) > .00001): t1Guess + ", " + t2Guess;
      assert (Math.abs(t1Guess.floatValue() - t3Guess.floatValue()) > .00001): t1Guess + ", " + t3Guess;
      assert (Math.abs(t2Guess.floatValue() - t3Guess.floatValue()) > .00001): t2Guess + ", " + t3Guess;
      
      logger.debug("t1Guess = "+ t1Guess.floatValue());
      logger.debug("t2Guess = "+ t2Guess.floatValue());
      logger.debug("t3Guess = "+ t3Guess.floatValue());
      
      assert (Math.abs(t1Guess.floatValue() + t2Guess.floatValue() - (2*t3Guess.floatValue()) ) < .00001);


   }
}
