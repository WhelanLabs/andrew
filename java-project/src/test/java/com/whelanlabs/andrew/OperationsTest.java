package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Node;

public class OperationsTest {

   private static Logger logger = LogManager.getLogger(OperationsTest.class);
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      App.initialize("linear_dataset_tests");
      App.getDataGraph().flush();
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      App.getDataGraph().cleanup();
   }

   //@Test(expected = IllegalArgumentException.class)
   public void traverse_badDirection_exception() {

      // setup data
      App.loadDataset(new LinearDataset());
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      
      Map<String, Object> inputs = new HashMap<>();
      inputs.put(startingNode.getKey() + "." + "startingNode", startingNode);
      inputs.put(startingNode.getKey() + "." + "direction", Direction.any.toString());
      inputs.put(startingNode.getKey() + "." + "traversalEdgeType", "LinearDatasetEdge");
      inputs.put(startingNode.getKey() + "." + "distance", 1);
      
      Map<String, Object> results = Operations.traverse(startingNode, inputs);
   }

   
   @Test
   public void traverse_negativeDirection_getResult() {

      // setup data
      App.loadDataset(new LinearDataset());
      Node startingNode = App.getDataGraph().getNodeByKey("LinearDatasetNode_500", "LinearDatasetNode");
      
      Map<String, Object> inputs = new HashMap<>();
      inputs.put(startingNode.getKey() + "." + "startingNode", startingNode);
      inputs.put(startingNode.getKey() + "." + "direction", Direction.inbound.toString());
      inputs.put(startingNode.getKey() + "." + "traversalEdgeType", "LinearDatasetEdge");
      inputs.put(startingNode.getKey() + "." + "distance", -1);
      
      Map<String, Object> results = Operations.traverse(startingNode, inputs);
      
      assert ("LinearDatasetNode_501".equals(((Node)results.get("RESULT")).getKey())): "{" + results + "}";
   }
}
