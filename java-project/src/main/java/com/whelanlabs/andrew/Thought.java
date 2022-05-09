package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

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
      
      List<List<Node>> layeredOperations = getOperationsByMaxLayer();
      
      // TODO: process the thought by layer
      
      return result;
   }

   private List<List<Node>> getOperationsByMaxLayer() {
      // Note: see p.14 of LBB for details.
      List<List<Node>> results = new ArrayList<>();
      
      Integer currentLevel = 0;
      List<Node> startingPoints = new ArrayList<>();
      Map<String, Integer> nodeMaxLevel = new HashMap<>();
      nodeMaxLevel.put(_thoughtNode.getKey() + ":" + _thoughtNode.getType(), currentLevel);
      startingPoints.add(_thoughtNode);

      while(startingPoints.size() > 0) {
         List<Node> nextStartingPoints = new ArrayList<>();
         currentLevel +=1;
         for(Node startingPoint : startingPoints) {
            List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(startingPoint, "thought_sequence", null, null);
            for(Triple<Node, Edge, Node> expansion : expansions) {
               Node right = expansion.getRight();
               nextStartingPoints.add(right);
               nodeMaxLevel.put(right.getKey() + ":" + right.getType(), currentLevel);
            }
         }
         startingPoints = nextStartingPoints;
      }
      Iterator<String> maxLevelIterator = nodeMaxLevel.keySet().iterator();
      while(maxLevelIterator.hasNext()) {
         String current = maxLevelIterator.next();
         String currentId = current.split(":")[0];
         Integer currentIdLevel = nodeMaxLevel.get(currentId);
         List<Node> nodeLevelcontents = results.get(currentIdLevel);
         
         // TODO: add the current node to the proper level
      }
      
      return null;
   }

   public String getKey() {
      // TODO Auto-generated method stub
      return _thoughtNode.getKey();
   }
}
