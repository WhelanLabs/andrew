package com.whelanlabs.andrew;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

   public static Map<String, Object> getNumberAttribute(Node node, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();
      Node targetNode = (Node)inputs.get(node.getKey() + "." + "targetNode");
      String attributeName = (String)inputs.get(node.getKey() + "." + "attributeName");
      String result = (String)targetNode.getAttribute(attributeName);
      results.put("RESULT", result);
      return results;
   }

   public static Map<String, Object> traverse(Node node, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();
      // TODO: implement.
      
      return results;
   }
   
   public static Map<String, Object> multiply(Node node, Map<String, Object> inputs) {
      logger.debug("multiply() ");
      logger.debug("node = " + node);
      logger.debug("inputs = " + inputs);
      Map<String, Object> results = new HashMap<>();
      Float floatA = ((Number)inputs.get(node.getKey() + "." + "floatA")).floatValue();
      Float floatB = ((Number)inputs.get(node.getKey() + "." + "floatB")).floatValue();
      Float result = floatA * floatB;
      results.put("RESULT", result);
      return results;
   }
   
   public static Map<String, Object> subtract(Node node, Map<String, Object> inputs) {
      Map<String, Object> results = new HashMap<>();
      Float floatA = (Float)inputs.get(node.getKey() + "." + "floatA");
      Float floatB = (Float)inputs.get(node.getKey() + "." + "floatB");
      logger.debug("subtract: " + floatA + " - " + floatB );
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
            results.put("traversalEdgeName", "edgeType");
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
