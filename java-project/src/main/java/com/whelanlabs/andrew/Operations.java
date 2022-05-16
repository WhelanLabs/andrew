package com.whelanlabs.andrew;

import java.util.HashMap;
import java.util.Map;

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

   
   
   public Operations(Node node) {
      _node = node;
   }

   public static Map<String, Object> getNumberAttribute(Map<String, Object> inputs) {
      return null;
   }

   public static Map<String, Object> traverse(Map<String, Object> inputs) {
      return null;
   }
   
   public static Map<String, Object> multiply(Map<String, Object> inputs) {
      return null;
   }
   
   public static Map<String, Object> subtract(Map<String, Object> inputs) {
      return null;
   }
   
   public static Map<String, Object> end(Map<String, Object> inputs) {
      return null;
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
            results.put("numberA", "number");
            results.put("numberB", "number");
            break;
         case "subtract":
            results.put("numberA", "number");
            results.put("numberB", "number");
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
