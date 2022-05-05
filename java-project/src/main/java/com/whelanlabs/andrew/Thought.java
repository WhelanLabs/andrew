package com.whelanlabs.andrew;

import java.util.List;
import java.util.Map;

import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

/**
 * The Class Thought.
 */
public class Thought {
   
   private String _thoughtID;

   public Thought(String thoughtID) {
      _thoughtID = thoughtID;
   }
   
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
      
      QueryClause queryClause = new QueryClause("_id", QueryClause.Operator.EQUALS, "bar");
      List<Node> results = App.getDataGraph().queryNodes("testType", queryClause);
      
      return result;
   }
}
