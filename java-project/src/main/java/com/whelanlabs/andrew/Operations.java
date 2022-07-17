package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

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

   private static Logger logger = LogManager.getLogger(Operations.class);

   private Operations() {
      // static class
   }

   public static Map<String, Object> traverse(Node currentNode, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();

      Node startingNode = (Node) inputs.get(currentNode.getKey() + "." + "startingNode");
      String direction = (String) inputs.get(currentNode.getKey() + "." + "direction");
      String relationType = (String) inputs.get(currentNode.getKey() + "." + "traversalEdgeType");
      Integer distance = ((Number) inputs.get(currentNode.getKey() + "." + "distance")).intValue();

      Node previousNode = traverse(startingNode, direction, relationType, distance);

      results.put("RESULT", previousNode);
      return results;
   }

   public static Node traverse(Node startingNode, String direction, String relationType, Integer distance) {
      logger.debug("traverse() ");
      logger.debug("   startingNode = " + startingNode);
      logger.debug("   direction = " + direction);
      logger.debug("   relationType = " + relationType);
      logger.debug("   distance = " + distance);

      // support negative distances
      if(distance <0) {
         if (Direction.outbound.toString().equals(direction)) {
            direction = Direction.inbound.toString();
         } else if (Direction.inbound.toString().equals(direction)) {
            direction = Direction.outbound.toString();
         }
         distance = distance * (-1);
      }

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
      return previousNode;
   }

   public static Map<String, Object> multiply(Node node, Map<String, Object> inputs) {
      logger.debug("multiply() ");
      Map<String, Object> results = new HashMap<>();
      Float floatA = ((Number) inputs.get(node.getKey() + "." + "floatA")).floatValue();
      Float floatB = ((Number) inputs.get(node.getKey() + "." + "floatB")).floatValue();
      Float result = floatA * floatB;
      results.put("RESULT", result);
      return results;
   }

   public static Map<String, Object> average(Node node, Map<String, Object> inputs) {
      logger.debug("average() ");
      Map<String, Object> results = new HashMap<>();
      
      Boolean hasInputs = true;
      ArrayList<Double> values = new ArrayList<>();
      Integer inputNumber = 1;
      while(hasInputs) {
         Number inputValue = (Number) inputs.get(node.getKey() + "." + "input" + inputNumber);
         if(null != inputValue) {
            Double value = inputValue.doubleValue();
            values.add(value);
            logger.debug("values = " + values);
            inputNumber++;
         }
         else {
            hasInputs = false;
         }
      }

      OptionalDouble result = values
            .stream()
            .mapToDouble(a -> a)
            .average();
      
      logger.debug("average.RESULT = " + result.getAsDouble());

      results.put("RESULT", result.getAsDouble());
      return results;
   }
   
   public static Map<String, Object> doNothing(Node node, Map<String, Object> inputs) {
      logger.debug("doNothing() ");
      Map<String, Object> results = new HashMap<>();
      return results;
   }
   
   /**
    * Subtract.
    *<p>
    *Assumes that <code>inputs</code> contains:
    *<ul>
    *<li>"floatA" as a <code>Number</code>
    *<li>"floatB" as a <code>Number</code>
    *</ul>
    *<p>
    *formula: <code>RESULT = floatA - floatB</code>
    *
    * @param node the node
    * @param inputs the inputs
    * @return the map
    */
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
      return inputs;
   }

}
