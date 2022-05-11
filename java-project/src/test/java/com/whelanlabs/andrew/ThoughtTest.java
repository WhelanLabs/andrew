package com.whelanlabs.andrew;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
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
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void forecast_simpleInputs_simpleOutputs() {
      // forecast given a simple thought and simple data.

      String rel = "next";
      Direction direction = Direction.outbound;
      Integer distance = 1;
      String targetProperty = "value";

      
      Thought initialTestThought = buildInitialTestThought();
      Thought thought = new Thought(initialTestThought.getKey());
      Node startingPoint = new Node(ElementHelper.generateKey(), ElementHelper.generateName());
      startingPoint.addAttribute(targetProperty, Float.valueOf("3.14159"));

      Goal goal = new Goal(rel, direction, distance, targetProperty);
      Node result = thought.forecast(startingPoint, goal);
      logger.debug("result = " + result);

      assert (result != null);
   }

   @Test
   public void buildThought_valid_success() throws Exception {
      
      App.getGardenGraph().flush();
      
      Long pre_thought_count = App.getGardenGraph().getCount("thought");
      Long pre_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
      Long pre_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
      Long pre_thought_result_count = App.getGardenGraph().getCount("thought_result");

      Long pre_count = pre_thought_count + pre_thought_operation_count + pre_thought_sequence_count + pre_thought_result_count;
      
      Thought initialThought = buildInitialTestThought();
      
      Long post_thought_count = App.getGardenGraph().getCount("thought");
      Long post_thought_operation_count = App.getGardenGraph().getCount("thought_operation");
      Long post_thought_sequence_count = App.getGardenGraph().getCount("thought_sequence");
      Long post_thought_result_count = App.getGardenGraph().getCount("thought_result");

      Long post_count = post_thought_count + post_thought_operation_count + post_thought_sequence_count + post_thought_result_count;
            
      assert (pre_count +14 == post_count): "{" + pre_count + ", " +post_count + "}";
      
      Integer entityComplexity = initialThought.getEntityComplexity();
      
      assert (post_count - pre_count == entityComplexity): "{" + post_count + ", " + pre_count + ", " + entityComplexity + "}";
   }

   @Test
   public void runThought_valid_success() throws Exception {
      Thought thought = buildModifiedInitialTestThought();
      
      App.getDataGraph().flush();
      App.loadDataset(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      Integer forwardDistance = 10;
      Goal goal = new Goal("LinearDatasetEdge", Direction.outbound, forwardDistance, "value");
      Node result = thought.forecast(startingNode, goal);
      
      assert (null != result);
      
      Integer resultValue = (Integer)result.getAttribute("value");
      assert (null != resultValue);
      logger.debug("resultValue = " + resultValue);
      Integer startingValue = (Integer)startingNode.getAttribute("value");
      assert (startingValue + forwardDistance == resultValue): "{" + startingValue + ", " + forwardDistance + ", " + resultValue + "}";
      
   }
   
   public Thought buildModifiedInitialTestThought() {
      // Note: see "modified initial example" flow from thought_process_language.html for reference.

      String thoughtKey = ElementHelper.generateKey();
      
      // create a thought node
      final Node thought = new Node(thoughtKey, "thought");

      // create steps
      final Node A1_getNumberAttribute = new Node(ElementHelper.generateKey(), "thought_operation");
      A1_getNumberAttribute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "getNumberAttribute");
      
      final Node B1_traverse = new Node(ElementHelper.generateKey(), "thought_operation");
      B1_traverse.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "traverse");

      final Node B2_getNumberAttrinbute = new Node(ElementHelper.generateKey(), "thought_operation");
      B2_getNumberAttrinbute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "getNumberAttrinbute");

      final Node A2_valueX2 = new Node(ElementHelper.generateKey(), "thought_operation");
      A2_valueX2.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "multiplication");

      final Node AB1_subtract = new Node(ElementHelper.generateKey(), "thought_operation");
      AB1_subtract.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "subtract");

      final Node end = new Node(ElementHelper.generateKey(), "thought_result");
      end.addAttribute("thought_key", thought.getKey());

      // link the thought process using valid sequence relationships
      Edge edge1 = new Edge(ElementHelper.generateKey(), thought, A1_getNumberAttribute, "thought_sequence");
      edge1.addAttribute("thought_key", thought.getKey());
      edge1.addAttribute("output", "GOAL.$targetProperty");
      
      Edge edge2 = new Edge(ElementHelper.generateKey(), A1_getNumberAttribute, A2_valueX2, "thought_sequence");
      edge2.addAttribute("thought_key", thought.getKey());
      Edge edge3 = new Edge(ElementHelper.generateKey(), A2_valueX2, AB1_subtract, "thought_sequence");
      edge3.addAttribute("thought_key", thought.getKey());
      Edge edge4 = new Edge(ElementHelper.generateKey(), thought, B1_traverse, "thought_sequence");
      edge4.addAttribute("thought_key", thought.getKey());
      Edge edge5 = new Edge(ElementHelper.generateKey(), B1_traverse, B2_getNumberAttrinbute, "thought_sequence");
      edge5.addAttribute("thought_key", thought.getKey());
      Edge edge6 = new Edge(ElementHelper.generateKey(), B2_getNumberAttrinbute, AB1_subtract, "thought_sequence");
      edge6.addAttribute("thought_key", thought.getKey());
      Edge edge7 = new Edge(ElementHelper.generateKey(), AB1_subtract, end, "thought_sequence");
      edge7.addAttribute("thought_key", thought.getKey());

      App.getGardenGraph().upsert(thought, A1_getNumberAttribute, B1_traverse, B2_getNumberAttrinbute, A2_valueX2, AB1_subtract, end);
      App.getGardenGraph().upsert(edge1, edge2, edge3, edge4, edge5, edge6, edge7);
      
      return new Thought(thoughtKey);
   }
   
   public Thought buildInitialTestThought() {
      // Note: see thought_process_language.html for reference

      String thoughtKey = ElementHelper.generateKey();
      
      // create a thought node
      final Node thought = new Node(thoughtKey, "thought");

      // create steps
      final Node A1_getNumberAttribute = new Node(ElementHelper.generateKey(), "thought_operation");
      A1_getNumberAttribute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "getNumberAttribute");
      
      final Node B1_traverse = new Node(ElementHelper.generateKey(), "thought_operation");
      B1_traverse.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "traverse");

      final Node B2_getNumberAttrinbute = new Node(ElementHelper.generateKey(), "thought_operation");
      B2_getNumberAttrinbute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "getNumberAttrinbute");

      final Node A2_valueX2 = new Node(ElementHelper.generateKey(), "thought_operation");
      A2_valueX2.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "multiplication");

      final Node AB1_subtract = new Node(ElementHelper.generateKey(), "thought_operation");
      AB1_subtract.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("thought_operation", "subtract");

      final Node end = new Node(ElementHelper.generateKey(), "thought_result");
      end.addAttribute("thought_key", thought.getKey());

      // link the thought process using valid sequence relationships
      Edge edge1 = new Edge(ElementHelper.generateKey(), thought, A1_getNumberAttribute, "thought_sequence");
      edge1.addAttribute("thought_key", thought.getKey());
      edge1.addAttribute("output", "GOAL.$targetProperty");
      
      Edge edge2 = new Edge(ElementHelper.generateKey(), A1_getNumberAttribute, A2_valueX2, "thought_sequence");
      edge2.addAttribute("thought_key", thought.getKey());
      Edge edge3 = new Edge(ElementHelper.generateKey(), A2_valueX2, AB1_subtract, "thought_sequence");
      edge3.addAttribute("thought_key", thought.getKey());
      Edge edge4 = new Edge(ElementHelper.generateKey(), thought, B1_traverse, "thought_sequence");
      edge4.addAttribute("thought_key", thought.getKey());
      Edge edge5 = new Edge(ElementHelper.generateKey(), B1_traverse, B2_getNumberAttrinbute, "thought_sequence");
      edge5.addAttribute("thought_key", thought.getKey());
      Edge edge6 = new Edge(ElementHelper.generateKey(), B2_getNumberAttrinbute, AB1_subtract, "thought_sequence");
      edge6.addAttribute("thought_key", thought.getKey());
      Edge edge7 = new Edge(ElementHelper.generateKey(), AB1_subtract, end, "thought_sequence");
      edge7.addAttribute("thought_key", thought.getKey());

      App.getGardenGraph().upsert(thought, A1_getNumberAttribute, B1_traverse, B2_getNumberAttrinbute, A2_valueX2, AB1_subtract, end);
      App.getGardenGraph().upsert(edge1, edge2, edge3, edge4, edge5, edge6, edge7);
      
      return new Thought(thoughtKey);
   }
   
   @Test
   public void getOperationsByMaxLayer_initialTestThought_success() throws Exception {
      
      //App.getGardenGraph().flush();
      Thought thought = buildInitialTestThought();
      Map<Integer, Set<String>> opsByLayer = thought.getOperationsByMaxLayer();
      
      assert (null != opsByLayer);
      assert (5 == opsByLayer.size()): opsByLayer;
      
      Set<String> layerTwo = opsByLayer.get(2);
      assert (2 == layerTwo.size()): "size = " + layerTwo.size() + ", contents = " + layerTwo;
   }
}
