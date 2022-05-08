package com.whelanlabs.andrew;

import java.util.List;
import java.util.Map;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

/**
 * The Class Thought.
 */
public class Thought {
   
   private Node _thoughtNode;
   private List<Node> _thoughtOperations;
   private List<Edge> _thoughtSequences;
   private Node _thoughtResult;

   public Thought(String thoughtKey) {
      // set the thought node
      _thoughtNode = App.getGardenGraph().getNodeByKey(thoughtKey, "thought");
      QueryClause queryClause = new QueryClause("thought_key", QueryClause.Operator.EQUALS, thoughtKey);
      
      // set the thought sequences
      _thoughtSequences = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
      
      // set the thought operations
      _thoughtOperations = App.getGardenGraph().queryNodes("thought_operation", queryClause);
      
      // set the thought result
      _thoughtResult = App.getGardenGraph().queryNodes("thought_result", queryClause).get(0);
   }
   
   public Integer getEntityComplexity() {
      Integer result = 0;
      
      if(_thoughtNode != null) {
         result += 1;
      }
      
      result += _thoughtOperations.size();
      result += _thoughtSequences.size();
      
      if(_thoughtResult != null) {
         result += 1;
      }
      
      return result;
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

   public String getKey() {
      // TODO Auto-generated method stub
      return _thoughtNode.getKey();
   }
}
