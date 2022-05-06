package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.List;

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
