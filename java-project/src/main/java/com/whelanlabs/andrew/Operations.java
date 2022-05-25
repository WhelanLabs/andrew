package com.whelanlabs.andrew;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

/*
 * TODO: This class is an abomination.  The contents of the methods and the 
 * descriptions for I/O interfaces should be managed within the thought_operation
 * nodes themselves.  Andrew should have no special knowledge of the operations,
 * just know if and where they are useful in constructing a good thought.
 * 
 * The post-version-one implementation should relegate Andrew as a processor
 * of thought operation logic.  (The likely implementation having Andrew
 * running a Groovy processor for the thought operation methods.)
 */

public class Operations {

   private Node _node;
   private static Logger logger = LogManager.getLogger(Operations.class);

   public Operations(Node node) {
      _node = node;
   }

   public static Map<String, Object> getNumberAttribute(Node currentNode, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();
      Node targetNode = (Node) inputs.get(currentNode.getKey() + "." + "targetNode");
      String attributeName = (String) inputs.get(currentNode.getKey() + "." + "attributeName");
      String result = (String) targetNode.getAttribute(attributeName);
      results.put("RESULT", result);
      return results;
   }

   public static Map<String, Object> traverse(Node currentNode, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();

      Node startingNode = (Node) inputs.get(currentNode.getKey() + "." + "startingNode");
      String direction = (String) inputs.get(currentNode.getKey() + "." + "direction");
      String relationType = (String) inputs.get(currentNode.getKey() + "." + "traversalEdgeType");
      Integer distance = ((Number) inputs.get(currentNode.getKey() + "." + "distance")).intValue();

      logger.debug("traverse() ");
      logger.debug("   startingNode = " + startingNode);
      logger.debug("   direction = " + direction);
      logger.debug("   relationType = " + relationType);
      logger.debug("   distance = " + distance);

      // TODO: support negative distances

      Node previousNode = startingNode;
      List<Triple<Node, Edge, Node>> expansions = null;
            
      
      for (int i = 0; i < distance; i++) {
         logger.debug("   traversal #" + i);
         if (Direction.outbound.toString().equals(direction)) {
            expansions = App.getDataGraph().expandRight(previousNode, relationType, null, null);
         } else if (Direction.inbound.toString().equals(direction)) {
            expansions = App.getDataGraph().expandLeft(previousNode, relationType, null, null);
         } else {
            throw new IllegalArgumentException("Invalid direction. (" + direction + ")");
         }
         previousNode = expansions.get(0).getRight();
      }

      results.put("RESULT", previousNode);
      return results;
   }

   public static Map<String, Object> multiply(Node node, Map<String, Object> inputs) {
      logger.debug("multiply() ");
      logger.debug("node = " + node);
      logger.debug("inputs = " + inputs);
      Map<String, Object> results = new HashMap<>();
      Float floatA = ((Number) inputs.get(node.getKey() + "." + "floatA")).floatValue();
      Float floatB = ((Number) inputs.get(node.getKey() + "." + "floatB")).floatValue();
      Float result = floatA * floatB;
      results.put("RESULT", result);
      return results;
   }

   public static Map<String, Object> subtract(Node node, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();
      
      Float floatA = ((Number) inputs.get(node.getKey() + "." + "floatA")).floatValue();
      Float floatB = ((Number) inputs.get(node.getKey() + "." + "floatB")).floatValue();
      logger.debug("subtract: " + floatA + " - " + floatB);
      logger.debug("   current node: " + node.getKey());

      Float result = floatA - floatB;
      results.put("RESULT", result);
      return results;
   }

   public static Map<String, Object> end(Node node, Map<String, Object> inputs) {
      logger.debug("RESULT = " + inputs);
      return inputs;
   }

   public static Map<String, String> listInputTypes(String operationName) {
      Map<String, String> results = new HashMap<>();

      switch (operationName) {
         case "getNumberAttribute":
            results.put("targetNode", "node");
            results.put("targetAttribute", "attributeName");
            break;
         case "traverse":
            results.put("startingNode", "node");
            results.put("traversalEdgeType", "edgeType");
            results.put("direction", "traversalDirection");
            results.put("distance", "integer");
            break;
         case "multiply":
            results.put("floatA", "float");
            results.put("floatB", "float");
            break;
         case "subtract":
            results.put("floatA", "float");
            results.put("floatB", "float");
            break;
         case "end":
            results.put("inputA", "object");
            break;
         default:
            throw new IllegalArgumentException("Invalid day of the week: ");
      }

      return results;
   }

}
