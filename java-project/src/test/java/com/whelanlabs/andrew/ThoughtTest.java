package com.whelanlabs.andrew;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Node;

public class ThoughtTest {

   private static Logger logger = LogManager.getLogger(ThoughtTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "andrew_test_database";
      App.initialize(databaseName);
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void forecast_simpleInputs_simpleOutputs() {
      // forecast given a simple thought and simple data.

//      String rel = "next";
//      Direction direction = Direction.outbound;
//      Integer distance = 1;
//      String targetProperty = "value";
//
//      
//      Thought initialTestThought = buildInitialTestThought();
//      Thought thought = new Thought(initialTestThought.getKey());
//      Node startingPoint = new Node(ElementHelper.generateKey(), ElementHelper.generateName());
//      startingPoint.addAttribute(targetProperty, Float.valueOf("3.14159"));
//
//      Goal goal = new Goal(rel, direction, distance, targetProperty);
//      Node result = thought.forecast(startingPoint, goal);
//      logger.debug("result = " + result);
//
//      assert (result != null);
   }

   @Test
   public void buildThought_valid_success() throws Exception {
      
      App.getGardenGraph().flush();
      
      Long pre_thought_count = App.getGardenGraph().getCount("thought");
      Long pre_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
      Long pre_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
      Long pre_thought_result_count = App.getGardenGraph().getCount("thought_result");

      Long pre_count = pre_thought_count + pre_thought_operation_count + pre_thought_sequence_count + pre_thought_result_count;
      
      TestHelper.buildInitialTestThought();
      
      Long post_thought_count = App.getGardenGraph().getCount("thought");
      Long post_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
      Long post_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
      Long post_thought_result_count = App.getGardenGraph().getCount("thought_result");

      Long post_count = post_thought_count + post_thought_operation_count + post_thought_sequence_count + post_thought_result_count;
            
      assert (pre_count +14 == post_count): "{" + pre_count + ", " +post_count + "}";
   }

   @Test
   public void runThought_valid_success() throws Exception {
      App.getDataGraph().flush();
      App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThought();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      Map<String, Object> result = thought.forecast(startingNode);
      
      assert (null != result);
      logger.debug("result = " + result);
      
      Number guess = (Number)result.get("RESULT.output");
      //logger.debug("guess = " + guess);
      
      Node answerNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_510", "LinearDatasetNode");
      Number answer = (Number)answerNode.getAttribute("value");
      //logger.debug("answer = " + answer);
      
      assert ( Math.abs(guess.floatValue() - answer.floatValue()) < 1): "{" + guess + ", " + answer + "}";
      
   }
   
   @Test(expected = RuntimeException.class)
   public void runThought_invalidNodeType_exception() throws Exception {
      App.getDataGraph().flush();
      App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithBadNode();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }
   
   @Test(expected = RuntimeException.class)
   public void runThought_noEnd_exception() throws Exception {
      App.getDataGraph().flush();
      App.getGardenGraph().flush();

      Thought thought = TestHelper.buildModifiedInitialTestThoughtWithNoEnd();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");

      thought.forecast(startingNode);
   }
   
  
   @Test
   public void getOperationsByMaxLayer_initialTestThought_success() throws Exception {
      
      //App.getGardenGraph().flush();
      Thought thought = TestHelper.buildInitialTestThought();
       List<Set<Node>> opsByLayer = thought.getOperationsByMaxLayer();
      
      assert (null != opsByLayer);
      assert (5 == opsByLayer.size()): opsByLayer;
      
      Set<Node> layerTwo = opsByLayer.get(2);
      assert (2 == layerTwo.size()): "size = " + layerTwo.size() + ", contents = " + layerTwo;
   }
}
