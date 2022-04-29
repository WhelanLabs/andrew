package com.whelanlabs.andrew;

import java.util.Map;

import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

/**
 * The Class Thought.
 */
public class Thought {

   
   /**
    * Forecast.
    * 
    * Makes a prediction given a goal.
    *
    * @param startingPoint the starting point
    * @param goal the goal
    * @return the node
    */
   public Node forecast(Node startingPoint, Goal goal) {
      Node result = new Node(ElementHelper.generateKey(), startingPoint.getType() );
      Map<String, Object> startingProps = startingPoint.getProperties();
      result.setProperties(startingProps);
      result.addAttribute("time", goal.getTargetProperty());
      
      return result;
   }
}
