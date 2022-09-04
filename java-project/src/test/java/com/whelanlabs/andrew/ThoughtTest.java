package com.whelanlabs.andrew;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.CSVLoader;
import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

import ch.qos.logback.classic.spi.ILoggingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

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
   public void forecast2_nullEdgeInput_errorMessage() throws Exception {
      // see: https://mincong.io/2020/02/02/logback-test-logging-event/
      ch.qos.logback.core.read.ListAppender<ILoggingEvent> appender = new ch.qos.logback.core.read.ListAppender<>();
      ch.qos.logback.classic.Logger appLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Thought.class);
      appender.start();
      appLogger.addAppender(appender);
      
      try {
         Thought thought = TestHelper.buildModifiedInitialTestThought();

         Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

         //Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(thought, startingNode);
         Map<String, Object> workingMemory = new HashMap<>();
         try {
            thought.forecast2(workingMemory);
         }
         catch (Exception e) {
            // expected. ignore.
         }
         
         Boolean msgFound = false;
         for(ILoggingEvent loggingEvent : appender.list) {
            String msg = loggingEvent.getMessage();
            logger.debug("msg = " + msg);
            if(msg.contains("Edge input value is NULL")) {
               msgFound = true;
               break;
            }
         }
         
         assert(true == msgFound);
      }
      finally {
         appLogger.detachAppender(appender);
      }

   }

   @Test
   public void forecast_multiplyThought_success() throws Exception {

      Float multiplier = 1.04f;

      Thought thought = TestHelper.buildMultiplicationThought(multiplier);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(thought, startingNode);
      Map<String, Object> result = thought.forecast2(workingMemory);

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

      Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(thought, startingNode);
      thought.forecast2(workingMemory);
   }

   @Test(expected = RuntimeException.class)
   public void forecast_noEnd_exception() throws Exception {

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(thought, startingNode);
      thought.forecast2(workingMemory);
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

      Map<String, Object> workingMemory1 = generateLegacyDataWorkingMemory(thought, startingNode);
      Map<String, Object> origResult = thought.forecast2(workingMemory1);

      Map<String, Object> workingMemory2 = generateLegacyDataWorkingMemory(clonedThought, startingNode);
      Map<String, Object> cloneResult = clonedThought.forecast2(workingMemory2);

      Number origGuess = (Number) origResult.get("RESULT.output");
      Number cloneGuess = (Number) cloneResult.get("RESULT.output");

      assert (Math.abs(origGuess.floatValue() - cloneGuess.floatValue()) < .01) : origGuess + ", " + cloneGuess;
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
      n1.addAttribute("name", "n1");
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("thought_key", n1.getKey());
      e0.addAttribute("name", "e0");
      App.getGardenGraph().upsert(goalNode, n1, e0);

      Thought thought = new Thought(n1);
      thought.clone();
   }

   @Test(expected = RuntimeException.class)
   public void clone_noApproach_getException() throws Exception {
      String thoughtKey = ElementHelper.generateKey();
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1");
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("thought_key", n1.getKey());
      e0.addAttribute("name", "e0");
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

      logger.debug("thought.exportJson() = " + thought.exportJson());
      logger.debug("clonedThought.exportJson() = " + clonedThought.exportJson());

      assert (null != thought);
      assert (null != clonedThought);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      logger.debug("startingNode = " + startingNode);

      Map<String, Object> workingMemory1 = generateLegacyDataWorkingMemory(thought, startingNode);
      Map<String, Object> origResult = thought.forecast2(workingMemory1);

      Map<String, Object> workingMemory2 = generateLegacyDataWorkingMemory(clonedThought, startingNode);
      Map<String, Object> cloneResult = clonedThought.forecast2(workingMemory2);

      Number origGuess = (Number) origResult.get("RESULT.output");
      Number cloneGuess = (Number) cloneResult.get("RESULT.output");

      assert (Math.abs(origGuess.floatValue() - cloneGuess.floatValue()) < .01) : origGuess + ", " + cloneGuess;

      Thought mutatedClonedThought = clonedThought.mutate(2);

      Map<String, Object> workingMemory3 = generateLegacyDataWorkingMemory(mutatedClonedThought, startingNode);
      Map<String, Object> mutatedCloneResult = mutatedClonedThought.forecast2(workingMemory3);

      Number mutatedCloneGuess = (Number) mutatedCloneResult.get("RESULT.output");

      /* 
       * Note: If the following assertion fails, run it again.  There is a small chance 
       * that the mutation is so small as to not be detected.
       */
      assert (Math.abs(origGuess.floatValue() - mutatedCloneGuess.floatValue()) > .00001) : origGuess + ", " + cloneGuess;

      logger.debug("origGuess = " + origGuess);
      logger.debug("mutatedCloneGuess = " + mutatedCloneGuess);
   }

   @Test
   public void merge_twoThoughts_combinedThought() throws Exception {

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);

      Merger merger = new SimpleMerger();
      Thought childThought = merger.merge(thought1, thought2);

      Map<String, Object> workingMemory1 = generateLegacyDataWorkingMemory(thought1, startingNode);
      Map<String, Object> t1Result = thought1.forecast2(workingMemory1);

      Map<String, Object> workingMemory2 = generateLegacyDataWorkingMemory(thought2, startingNode);
      Map<String, Object> t2Result = thought2.forecast2(workingMemory2);

      logger.debug("### Start: Forcasting the Child Thought ###");
      Map<String, Object> workingMemory3 = generateLegacyDataWorkingMemory(childThought, startingNode);
      Map<String, Object> t3Result = childThought.forecast2(workingMemory3);
      Number t1Guess = (Number) t1Result.get("RESULT.output");
      Number t2Guess = (Number) t2Result.get("RESULT.output");
      Number t3Guess = (Number) t3Result.get("RESULT.output");

      assert (Math.abs(t1Guess.floatValue() - t2Guess.floatValue()) > .00001) : t1Guess + ", " + t2Guess;
      assert (Math.abs(t1Guess.floatValue() - t3Guess.floatValue()) > .00001) : t1Guess + ", " + t3Guess;
      assert (Math.abs(t2Guess.floatValue() - t3Guess.floatValue()) > .00001) : t2Guess + ", " + t3Guess;

      logger.debug("t1Guess = " + t1Guess.floatValue());
      logger.debug("t2Guess = " + t2Guess.floatValue());
      logger.debug("t3Guess = " + t3Guess.floatValue());

      assert (Math.abs(t1Guess.floatValue() + t2Guess.floatValue() - (2 * t3Guess.floatValue())) < .00001);
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

      Map<String, Object> workingMemory1 = generateLegacyDataWorkingMemory(thought1, startingNode);
      Map<String, Object> t1Result = thought1.forecast2(workingMemory1);

      Map<String, Object> workingMemory2 = generateLegacyDataWorkingMemory(thought2, startingNode);
      Map<String, Object> t2Result = thought2.forecast2(workingMemory2);

      Map<String, Object> workingMemory3 = generateLegacyDataWorkingMemory(child1Thought, startingNode);
      Map<String, Object> t3Result = child1Thought.forecast2(workingMemory3);

      Map<String, Object> workingMemory4 = generateLegacyDataWorkingMemory(child2Thought, startingNode);
      Map<String, Object> t4Result = child2Thought.forecast2(workingMemory4);

      Map<String, Object> workingMemory5 = generateLegacyDataWorkingMemory(grandchildThought, startingNode);
      Map<String, Object> grandchildResult = grandchildThought.forecast2(workingMemory5);

      Number t1Guess = (Number) t1Result.get("RESULT.output");
      Number t2Guess = (Number) t2Result.get("RESULT.output");
      Number t3Guess = (Number) t3Result.get("RESULT.output");
      Number t4Guess = (Number) t4Result.get("RESULT.output");
      Number grandchildGuess = (Number) grandchildResult.get("RESULT.output");

      logger.debug("t1Guess = " + t1Guess.floatValue());
      logger.debug("t2Guess = " + t2Guess.floatValue());
      logger.debug("t3Guess = " + t3Guess.floatValue());
      logger.debug("t4Guess = " + t4Guess.floatValue());
      logger.debug("grandchildGuess = " + grandchildGuess.floatValue());

      assert (Math.abs(t3Guess.floatValue() + t4Guess.floatValue() - (2 * grandchildGuess.floatValue())) < .00001);
   }

   @Test
   public void exportJson_goodThought_goodJson() throws Exception {

      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);

      Merger merger = new SimpleMerger();
      Thought child1Thought = merger.merge(thought1, thought2);

      String jsonString = child1Thought.exportJson();

      assert (null != jsonString);

      logger.debug("jsonString = " + jsonString);

      assert (jsonString.contains("\"properties\" : {"));
      assert (jsonString.contains("\"name\" : \"n1\","));
      assert (jsonString.contains("\"_id\" : \"thought_operation/KEY_"));
      assert (jsonString.contains("\"_id\" : \"approach/KEY_"));
      assert (jsonString.contains("\"_id\" : \"thought_sequence/KEY_"));
      assert (jsonString.contains("\"_id\" : \"goal/KEY_"));

      String dirString = "./target/exports/";
      String fileString = dirString + "exportJson_goodThought_goodJson.json";
      Path path = Paths.get(dirString);
      Files.createDirectories(path);
      try (PrintWriter out = new PrintWriter(fileString)) {
         out.println(jsonString);
      }
   }

   @Test
   public void exportJson_linearGrowthThought_goodJson() throws Exception {

      Thought thought1 = TestHelper.buildModifiedInitialTestThought();

      String jsonString = thought1.exportJson();

      assert (null != jsonString);

      String dirString = "./target/exports/";
      String fileString = dirString + "linear_growth_thought.json";
      Path path = Paths.get(dirString);
      Files.createDirectories(path);
      try (PrintWriter out = new PrintWriter(fileString)) {
         out.println(jsonString);
      }
   }

   @Test
   public void exportDot_goodThought_goodDot() throws Exception {

      Thought thought1 = TestHelper.buildModifiedInitialTestThought();
      Thought thought2 = TestHelper.buildMultiplicationThought(thought1.getGoal(), 1.5f);

      Merger merger = new SimpleMerger();
      Thought child1Thought = merger.merge(thought1, thought2);

      String dotString = child1Thought.exportDot();

      assert (null != dotString);

      logger.debug("dotString = " + dotString);

      assert (dotString.contains("digraph G {"));
      assert (dotString.contains("node [shape=record fontname=Arial];"));
      assert (dotString.contains("\\\"\\lname = \\\"n1\\\"\\l__type = \\\"thought\\\"\\l\"]"));
      assert (dotString.contains(" [label=\"type = \\\"thought_operation\\\"\\lid = \\\"thought_operation/KEY_"));
      assert (dotString.contains("[shape=oval label=\"type = \\\"thought_sequence\\\"\\lid = \\\"thought_sequence/KEY_"));
      assert (dotString.contains(" -> thought_sequence_KEY_"));
      assert (dotString.contains(" -> thought_operation_KEY_"));
      assert (dotString.contains(" [label=\"type = \\\"thought_result\\\"\\lid = \\\"thought_result/KEY_"));

      String dirString = "./target/exports/";
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

      Thought t = App.loadThoughtFromJson("test_load_data", content);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(t, startingNode);
      Map<String, Object> result = t.forecast2(workingMemory);

      Number guess = (Number) result.get("RESULT.output");

      assert (guess.intValue() == 641) : "guess = " + guess;
   }

   @Test
   public void importJson_linearGrowthThought_loaded() throws Exception {
      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));

      Thought t = App.loadThoughtFromJson("linear_growth_thought", content);

      // TODO: modify the goal to specify the startDate and symbol

      // load the test data
      // flushing should not be necessary
      // App.getDataGraph().flush();
      List<File> files = new ArrayList<>();
      files.add(new File("../fetchers/stock_data_fetcher/data/AACG_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AAPL_2020-05-07.txt"));
      CSVLoader stockLoader = new CSVLoader();
      stockLoader.loadStocks(files);

      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> workingMemory = generateLegacyDataWorkingMemory(t, startingNode);
      // 14614 ~= Jan 5, 2010
      workingMemory.put("GOAL.startDate", 14614);
      workingMemory.put("GOAL.symbol", "AAPL");
      Map<String, Object> result = t.forecast2(workingMemory);

      Number guess = (Number) result.get("RESULT.output");

      assert (guess.intValue() - 42.93 < 0.01) : "guess = " + guess;
   }

   @Test
   public void forecast2_valid_success() throws Exception {

      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));
      Thought thought = App.loadThoughtFromJson("linear_growth_thought", content);

      List<File> files = new ArrayList<>();
      files.add(new File("../fetchers/stock_data_fetcher/data/AAPL_2020-05-07.txt"));
      CSVLoader stockLoader = new CSVLoader();
      stockLoader.loadStocks(files);

      // populate starting conditions
      Map<String, Object> workingMemory = new HashMap<>();
      workingMemory = thought.addContext(workingMemory, "symbol", "AAPL", "GOAL");
      workingMemory = thought.addContext(workingMemory, "distance", 90, "GOAL");
      workingMemory = thought.addContext(workingMemory, "targetProperty", "dayClose", "GOAL");

      // 14614 ~= Jan 5, 2010
      workingMemory = thought.addContext(workingMemory, "startDate", 14614, "GOAL");

      logger.debug("workingMemory = " + workingMemory);

      Map<String, Object> result = thought.forecast2(workingMemory);

      assert (null != result);
      logger.debug("result = " + result);

      Number guess = (Number) result.get("RESULT.output");

      assert (Math.abs(guess.floatValue() - 34.072853) < 0.01) : guess;
   }

   public Map<String, Object> generateLegacyDataWorkingMemory(Thought thought, Node startingNode) {
      String targetPropName = (String) thought.getGoal().getAttribute("targetProperty");
      Object startingTargetPropValue = startingNode.getAttribute(targetPropName);
      Integer distance = (Integer) thought.getGoal().getAttribute("distance");
      String direction = (String) thought.getGoal().getAttribute("direction");
      String relationType = (String) thought.getGoal().getAttribute("relationType");

      Map<String, Object> workingMemory = new HashMap<>();
      workingMemory = thought.addContext(workingMemory, "startingNode", startingNode, "GOAL");
      workingMemory = thought.addContext(workingMemory, startingNode.getProperties(), startingNode.getKey());
      workingMemory = thought.addContext(workingMemory, thought.getGoal().getProperties(), "GOAL");
      workingMemory = thought.addContext(workingMemory, "targetPropValue", startingTargetPropValue, thought.getThoughtNode().getKey());
      workingMemory = thought.addContext(workingMemory, "distance", distance, thought.getThoughtNode().getKey());
      workingMemory = thought.addContext(workingMemory, "direction", direction, thought.getThoughtNode().getKey());
      workingMemory = thought.addContext(workingMemory, "startingNode", startingNode, thought.getThoughtNode().getKey());
      workingMemory = thought.addContext(workingMemory, "relationType", relationType, thought.getThoughtNode().getKey());

      return workingMemory;
   }
   
   @Test
   public void addGoalAttributes_newAttr_added() throws Exception {
      Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      Thought thought = TestHelper.buildModifiedInitialTestThought();
      Map<String, Object> workingMemory = new HashMap<>();
      Node goal = new Node(ElementHelper.generateKey(), "goal");
      goal.addAttribute("foo", "bar");
      workingMemory = thought.addGoalAttributes(workingMemory, goal);
      
      assert ("bar".equals(workingMemory.get("GOAL.foo"))) : workingMemory;
   }
   
}
