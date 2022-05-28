package com.whelanlabs.andrew;

import java.util.List;
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
import com.whelanlabs.kgraph.engine.QueryClause;

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
      
      Thought initialThought = buildInitialTestThought();
      
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

      Thought thought = buildModifiedInitialTestThought();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      Integer forwardDistance = 10;
      // Goal goal = new Goal("LinearDatasetEdge", Direction.outbound, forwardDistance, "value");
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

      Thought thought = buildModifiedInitialTestThoughtWithBadNode();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      Integer forwardDistance = 10;
      // Goal goal = new Goal("LinearDatasetEdge", Direction.outbound, forwardDistance, "value");
      Map<String, Object> result = thought.forecast(startingNode);
   }
   
   @Test(expected = RuntimeException.class)
   public void runThought_noEnd_exception() throws Exception {
      App.getDataGraph().flush();
      App.getGardenGraph().flush();

      Thought thought = buildModifiedInitialTestThoughtWithNoEnd();
      
      App.loadDatasetToDataGraph(new LinearDataset());
      
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      Integer forwardDistance = 10;
      // Goal goal = new Goal("LinearDatasetEdge", Direction.outbound, forwardDistance, "value");
      Map<String, Object> result = thought.forecast(startingNode);
   }
   
   
   private Thought buildModifiedInitialTestThoughtWithNoEnd() {
      String thoughtKey = ElementHelper.generateKey();
      
      //create the goal
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      goalNode.addAttribute("relationType", "LinearDatasetEdge");
      goalNode.addAttribute("direction", Direction.outbound.toString());
      goalNode.addAttribute("name", "goalNode" );
      goalNode.addAttribute("distance", 10);
      goalNode.addAttribute("targetProperty", "value");
      goalNode.addAttribute("resultClass", "Float");

      
      // create a thought node
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );

      // create steps
      final Node n2 = new Node(ElementHelper.generateKey(), "thought_operation");
      n2.addAttribute("thought_key", n1.getKey());
      n2.addAttribute("name", "n2" );
      n2.addAttribute("operationName", "doNothing");
      
      Edge e1 = new Edge(ElementHelper.generateKey(), n1, n2, "thought_sequence");
      e1.addAttribute("thought_key", n1.getKey());
      e1.addAttribute("name", "e1" );
      e1.addAttribute("input", "targetPropValue" );
      e1.addAttribute("output", "floatA");
      
      // link the thought process using valid sequence relationships
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("name", "e0" );
      
      App.getGardenGraph().upsert(goalNode, n1, n2);
      App.getGardenGraph().upsert(e0, e1);
      
      return new Thought(thoughtKey);
   }

   private Thought buildModifiedInitialTestThoughtWithBadNode() {
      String thoughtKey = ElementHelper.generateKey();
      
      //create the goal
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      goalNode.addAttribute("relationType", "LinearDatasetEdge");
      goalNode.addAttribute("direction", Direction.outbound.toString());
      goalNode.addAttribute("name", "goalNode" );
      goalNode.addAttribute("distance", 10);
      goalNode.addAttribute("targetProperty", "value");
      goalNode.addAttribute("resultClass", "Float");

      
      // create a thought node
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );

      // create steps
      final Node n2 = new Node(ElementHelper.generateKey(), "bad_type");
      n2.addAttribute("thought_key", n1.getKey());
      n2.addAttribute("name", "n2" );
      n2.addAttribute("operationName", "multiply");
      
      Edge e1 = new Edge(ElementHelper.generateKey(), n1, n2, "thought_sequence");
      e1.addAttribute("thought_key", n1.getKey());
      e1.addAttribute("name", "e1" );
      e1.addAttribute("input", "targetPropValue" );
      e1.addAttribute("output", "floatA");
      
      // link the thought process using valid sequence relationships
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("name", "e0" );
      
      App.getGardenGraph().upsert(goalNode, n1, n2);
      App.getGardenGraph().upsert(e0, e1);
      
      return new Thought(thoughtKey);
   }

   public Thought buildModifiedInitialTestThought() {
      // Note: see "modified initial example" flow from thought_process_language.html for reference.

      String thoughtKey = ElementHelper.generateKey();
      
      //create the goal
      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      goalNode.addAttribute("relationType", "LinearDatasetEdge");
      goalNode.addAttribute("direction", Direction.outbound.toString());
      goalNode.addAttribute("name", "goalNode" );
      goalNode.addAttribute("distance", 10);
      goalNode.addAttribute("targetProperty", "value");
      goalNode.addAttribute("resultClass", "Float");

      
      // create a thought node
      final Node n1 = new Node(thoughtKey, "thought");
      n1.addAttribute("name", "n1" );

      // create steps
      final Node n2 = new Node(ElementHelper.generateKey(), "thought_operation");
      n2.addAttribute("thought_key", n1.getKey());
      n2.addAttribute("name", "n2" );
      n2.addAttribute("operationName", "multiply");
      
      final Node n3 = new Node(ElementHelper.generateKey(), "thought_operation");
      n3.addAttribute("thought_key", n1.getKey());
      n3.addAttribute("name", "n3" );
      n3.addAttribute("operationName", "traverse");
      
      final Node n4 = new Node(ElementHelper.generateKey(), "thought_operation");
      n4.addAttribute("thought_key", n1.getKey());
      n4.addAttribute("name", "n4" );
      n4.addAttribute("operationName", "subtract");

      final Node n5 = new Node(ElementHelper.generateKey(), "thought_result");
      n5.addAttribute("thought_key", n1.getKey());
      n5.addAttribute("name", "n5" );
      n5.addAttribute("operationName", "end");

      final Node n6 = new Node(ElementHelper.generateKey(), "thought_operation");
      n6.addAttribute("thought_key", n1.getKey());
      n6.addAttribute("name", "n6" );
      n6.addAttribute("operationName", "multiply");
      
      // link the thought process using valid sequence relationships
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
      e0.addAttribute("name", "e0" );
      
      Edge e1 = new Edge(ElementHelper.generateKey(), n1, n2, "thought_sequence");
      e1.addAttribute("thought_key", n1.getKey());
      e1.addAttribute("name", "e1" );
      e1.addAttribute("input", "targetPropValue" );
      e1.addAttribute("output", "floatA");
      
      Edge e2 = new Edge(ElementHelper.generateKey(), n1, n2, "thought_sequence");
      e2.addAttribute("thought_key", n1.getKey());
      e2.addAttribute("name", "e2" );
      e2.addAttribute("input", "NUMBER.2" );
      e2.addAttribute("output", "floatB");

      Edge e3 = new Edge(ElementHelper.generateKey(), n1, n3, "thought_sequence");
      e3.addAttribute("thought_key", n1.getKey());
      e3.addAttribute("name", "e3" );
      e3.addAttribute("input", "startingNode" );
      e3.addAttribute("output", "startingNode");
      
      Edge e4 = new Edge(ElementHelper.generateKey(), n1, n3, "thought_sequence");
      e4.addAttribute("thought_key", n1.getKey());
      e4.addAttribute("name", "e4" );
      e4.addAttribute("input", "relationType" );
      e4.addAttribute("output", "traversalEdgeType");
      
      Edge e5 = new Edge(ElementHelper.generateKey(), n1, n3, "thought_sequence");
      e5.addAttribute("thought_key", n1.getKey());
      e5.addAttribute("name", "e5" );
      e5.addAttribute("input", "direction" );
      e5.addAttribute("output", "direction");

      Edge e6 = new Edge(ElementHelper.generateKey(), n1, n6, "thought_sequence");
      e6.addAttribute("thought_key", n1.getKey());
      e6.addAttribute("name", "e6" );
      e6.addAttribute("input", "distance" );
      e6.addAttribute("output", "floatA");
      
      Edge e7 = new Edge(ElementHelper.generateKey(), n2, n4, "thought_sequence");
      e7.addAttribute("thought_key", n1.getKey());
      e7.addAttribute("name", "e7" );
      e7.addAttribute("input", "RESULT" );
      e7.addAttribute("output", "floatA");

      Edge e8 = new Edge(ElementHelper.generateKey(), n3, n4, "thought_sequence");
      e8.addAttribute("thought_key", n1.getKey());
      e8.addAttribute("name", "e8" );
      e8.addAttribute("input", "RESULT.value" );
      e8.addAttribute("output", "floatB");
      
      Edge e9 = new Edge(ElementHelper.generateKey(), n4, n5, "thought_sequence");
      e9.addAttribute("thought_key", n1.getKey());
      e9.addAttribute("name", "e9" );
      e9.addAttribute("input", "RESULT" );
      e9.addAttribute("output", "output");

      Edge e10 = new Edge(ElementHelper.generateKey(), n1, n6, "thought_sequence");
      e10.addAttribute("thought_key", n1.getKey());
      e10.addAttribute("name", "e10" );
      e10.addAttribute("input", "NUMBER.-1" );
      e10.addAttribute("output", "floatB");
      
      Edge e11 = new Edge(ElementHelper.generateKey(), n6, n3, "thought_sequence");
      e11.addAttribute("thought_key", n1.getKey());
      e11.addAttribute("name", "e11" );
      e11.addAttribute("input", "RESULT" );
      e11.addAttribute("output", "distance");
      
      
      App.getGardenGraph().upsert(goalNode, n1, n2, n3, n4, n5, n6);
      App.getGardenGraph().upsert(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11);
      
      return new Thought(thoughtKey);
   }
   
   public Thought buildInitialTestThought() {
      // Note: see thought_process_language.html for reference

      String thoughtKey = ElementHelper.generateKey();
      
      // create a thought node
      final Node thought = new Node(thoughtKey, "thought");

      final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
      goalNode.addAttribute("relationType", "LinearDatasetEdge");
      goalNode.addAttribute("direction", Direction.outbound.toString());
      goalNode.addAttribute("distance", 10);
      goalNode.addAttribute("targetProperty", "value");
      
      Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, thought, "approach");
      
      // create steps
      final Node A1_getNumberAttribute = new Node(ElementHelper.generateKey(), "thought_operation");
      A1_getNumberAttribute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("operationName", "getNumberAttribute");
      
      final Node B1_traverse = new Node(ElementHelper.generateKey(), "thought_operation");
      B1_traverse.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("operationName", "traverse");

      final Node B2_getNumberAttrinbute = new Node(ElementHelper.generateKey(), "thought_operation");
      B2_getNumberAttrinbute.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("operationName", "getNumberAttrinbute");

      final Node A2_valueX2 = new Node(ElementHelper.generateKey(), "thought_operation");
      A2_valueX2.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("operationName", "multiply");

      final Node AB1_subtract = new Node(ElementHelper.generateKey(), "thought_operation");
      AB1_subtract.addAttribute("thought_key", thought.getKey());
      A1_getNumberAttribute.addAttribute("operationName", "subtract");

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

      App.getGardenGraph().upsert(goalNode, thought, A1_getNumberAttribute, B1_traverse, B2_getNumberAttrinbute, A2_valueX2, AB1_subtract, end);
      App.getGardenGraph().upsert(e0, edge1, edge2, edge3, edge4, edge5, edge6, edge7);
      
      return new Thought(thoughtKey);
   }
   
   @Test
   public void getOperationsByMaxLayer_initialTestThought_success() throws Exception {
      
      //App.getGardenGraph().flush();
      Thought thought = buildInitialTestThought();
       List<Set<Node>> opsByLayer = thought.getOperationsByMaxLayer();
      
      assert (null != opsByLayer);
      assert (5 == opsByLayer.size()): opsByLayer;
      
      Set<Node> layerTwo = opsByLayer.get(2);
      assert (2 == layerTwo.size()): "size = " + layerTwo.size() + ", contents = " + layerTwo;
   }
}
