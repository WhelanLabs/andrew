package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

   @Test
   public void forecast_valid_success() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThought();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> result = thought.forecast(startingNode);

      assert (null != result);
      logger.debug("result = " + result);

      Number guess = (Number) result.get("RESULT.output");

      Node answerNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_510", "LinearDatasetNode");
      Number answer = (Number) answerNode.getAttribute("value");

      assert (Math.abs(guess.floatValue() - answer.floatValue()) < 1) : "{" + guess + ", " + answer + "}";
   }

   @Test
   public void forecast_multiplyThought_success() throws Exception {

      Float multiplier = 1.04f;

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
   public void forecast_invalidNodeType_exception() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithBadNode();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }

   @Test(expected = RuntimeException.class)
   public void forecast_noEnd_exception() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }

   @Test
   public void getOperationsByMaxLayer_initialTestThought_success() throws Exception {
      
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
      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();
      thought.clone();
   }
   
   @Test(expected = RuntimeException.class)
   public void clone_noResult_getException() throws Exception {
      String thoughtKey = ElementHelper.generateKey();
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("thought_key", n1.getKey());
      e0.addAttribute("name", "e0" );
      App.getGardenGraph().upsert(goalNode, n1, e0);
      
      Thought thought = new Thought(n1);
      thought.clone();
   }
   
   @Test(expected = RuntimeException.class)
   public void clone_noApproach_getException() throws Exception {
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
            
      thought.clone();
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
   
   @Test
   public void merge_twoMergedThoughts_combinedThought() throws Exception {

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
            
      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);
      Thought thought3 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 3.5f);
      
      Merger merger = new SimpleMerger();
      Thought child1Thought = merger.merge(thought1, thought2);
      Thought child2Thought = merger.merge(thought1, thought3);
      Thought grandchildThought = merger.merge(child1Thought, child2Thought);

      Map<String, Object> t1Result = thought1.forecast(startingNode);
      Map<String, Object> t2Result = thought2.forecast(startingNode);
      Map<String, Object> t3Result = child1Thought.forecast(startingNode);
      Map<String, Object> t4Result = child2Thought.forecast(startingNode);
      Map<String, Object> grandchildResult = grandchildThought.forecast(startingNode);
      
      Number t1Guess = (Number) t1Result.get("RESULT.output");
      Number t2Guess = (Number) t2Result.get("RESULT.output");
      Number t3Guess = (Number) t3Result.get("RESULT.output");
      Number t4Guess = (Number) t4Result.get("RESULT.output");
      Number grandchildGuess = (Number) grandchildResult.get("RESULT.output");
      
      logger.debug("t1Guess = "+ t1Guess.floatValue());
      logger.debug("t2Guess = "+ t2Guess.floatValue());
      logger.debug("t3Guess = "+ t3Guess.floatValue());
      logger.debug("t4Guess = "+ t4Guess.floatValue());
      logger.debug("grandchildGuess = "+ grandchildGuess.floatValue());
      
      assert (Math.abs(t3Guess.floatValue() + t4Guess.floatValue() - (2*grandchildGuess.floatValue()) ) < .00001);
   }
   
   @Test
   public void exportJson_goodThought_goodJson() throws Exception {
            
      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);
      
      Merger merger = new SimpleMerger();
      Thought child1Thought = merger.merge(thought1, thought2);
      
      String jsonString = child1Thought.exportJson();

      assert (null != jsonString);
      
      logger.debug("jsonString = "+ jsonString);
      
      assert (jsonString.contains("\"properties\" : {"));
      assert (jsonString.contains("\"name\" : \"n1\","));
      assert (jsonString.contains("\"id\" : \"thought_operation/KEY_"));
      assert (jsonString.contains("\"id\" : \"approach/KEY_"));
      assert (jsonString.contains("\"id\" : \"thought_sequence/KEY_"));
   }
   
   @Test
   public void exportDot_goodThought_goodDot() throws Exception {
            
      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);
      
      Merger merger = new SimpleMerger();
      Thought child1Thought = merger.merge(thought1, thought2);
      
      String dotString = child1Thought.exportDot();

      assert (null != dotString);
      
      logger.debug("dotString = "+ dotString);
      
      assert (dotString.contains("digraph G {"));
      assert (dotString.contains("node [shape=record fontname=Arial];"));
      assert (dotString.contains("\\\"\\lname = \\\"n1\\\"\\l__type = \\\"thought\\\"\\l\"]"));
      assert (dotString.contains(" [label=\"type = \\\"thought_operation\\\"\\lid = \\\"thought_operation/KEY_"));
      assert (dotString.contains("[shape=oval label=\"type = \\\"thought_sequence\\\"\\lid = \\\"thought_sequence/KEY_"));
      assert (dotString.contains(" -> thought_sequence_KEY_"));
      assert (dotString.contains(" -> thought_operation_KEY_"));
      assert (dotString.contains(" [label=\"type = \\\"thought_result\\\"\\lid = \\\"thought_result/KEY_"));

      String dirString = "./target/dot_files/";
      String fileString = dirString + "exportDot_goodThought_goodDot.dot";
      Path path = Paths.get(dirString);
      Files.createDirectories(path);
      try (PrintWriter out = new PrintWriter(fileString)) {
         out.println(dotString);
     }
   }
   
   @Test
   public void importJson_goodThought_loaded() throws Exception {
      // hint: use andrew\java-project\src\test\resources/test_load_data.json
      String filePath = "./src/test/resources/test_load_data.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));

      // logger.debug("content = "+ content);

      Thought t = App.loadThoughtFromJson(content);
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> result = t.forecast(startingNode);
      
      Number guess = (Number) result.get("RESULT.output");

      assert (guess.intValue() == 3) : "guess = " + guess ;
      fail("implement me!");
   }
}
