package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.List;

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

public class ThoughtOperation {

   public static Object getNumberAttribute(List<Object> inputs) {
      return null;
   }

   public static List<String> listInputTypes(String operationName) {
      List<String> results = new ArrayList<>();

      switch (operationName) {
         case "getNumberAttribute":
            results.add("meh");
            break;
         case "Tuesday":
            results.add("meh");
            break;
         default:
            throw new IllegalArgumentException("Invalid day of the week: ");
      }

      return results;
   }

}
