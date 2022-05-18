package com.whelanlabs.andrew;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
   private Node _goal;

   private static Logger logger = LogManager.getLogger(Thought.class);

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

      // set the thought's goal
      List<Triple<Node, Edge, Node>> triple = App.getGardenGraph().expandLeft(_thoughtNode, "approach", null, null);
      _goal = triple.get(0).getRight();
   }

   public Integer getEntityComplexity() {
      Integer result = 0;

      if (_thoughtNode != null) {
         result += 1;
      }

      result += _thoughtOperations.size();
      result += _thoughtSequences.size();

      if (_thoughtResult != null) {
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
    * @throws Exception 
    */
   public Object forecast(Node startingPoint) throws Exception {
      Map<String, Object> workingMemory = new HashMap<>();

      Object result = null;

      // get the initial layer inputs from the goal
      workingMemory = addContext(workingMemory, startingPoint.getProperties(), startingPoint.getKey());
      workingMemory = addContext(workingMemory, _goal.getProperties(), "GOAL");

      List<Set<Node>> layeredOperations = getOperationsByMaxLayer();

      // process the thought by layer...
      for (Set<Node> currentOperations : layeredOperations) {
         // process the nodes of a layer
         logger.debug("layer contents = " + currentOperations);
         Set<String> nextLevelInputNodeKeys = new HashSet<>();
         for (Node node : currentOperations) {
            if ("thought_operation".equals(node.getType())) {
               Map<String, Object> opResult = processOperation(node, workingMemory);
               workingMemory = addContext(workingMemory, opResult, node.getKey());
               nextLevelInputNodeKeys.add(node.getKey());
            } else if ("thought".equals(node.getType())) {
               List<Triple<Node, Edge, Node>> goalTriples = App.getGardenGraph().expandLeft(node, "approach", null, null);
               Node goal = goalTriples.get(0).getRight();
               String targetPropName = (String) goal.getAttribute("targetProperty");
               logger.debug("targetPropName = " + targetPropName);
               Object startingTargetPropValue = startingPoint.getAttribute(targetPropName);
               workingMemory = addContext(workingMemory, "targetPropValue", startingTargetPropValue, node.getKey());
            }

            // getInputs
            for (String nextLevelInputNodeKey : nextLevelInputNodeKeys) {
               QueryClause queryClause = new QueryClause("_right", QueryClause.Operator.EQUALS, node.getKey());
               List<Edge> inputEdges = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
               for (Edge inputEdge : inputEdges) {
                  String inputProp = (String) inputEdge.getAttribute("input");
                  String outputProp = (String) inputEdge.getAttribute("output");
                  Object value = workingMemory.get(inputEdge.getAttribute("_left") + "." + inputProp);
                  String rightKey = (String) inputEdge.getAttribute("_right");
                  workingMemory.put(rightKey + "." + outputProp, value);
               }
            }
         }

         // get the inputs for the next layer via edge processing
      }

      return result;
   }

   private Map<String, Object> processOperation(Node node, Map<String, Object> workingMemory) throws Exception {
      String operationName = (String) node.getAttribute("operationName");
      // Map<String, Object> inputs = getOperationInputs(node, workingMemory);
      logger.debug("operation Name = " + operationName);
      logger.debug("workingMemory = " + workingMemory);

      // reflection to call the method with inputs.
      Method operationMethod = Operations.class.getMethod(operationName, Node.class, Map.class);

      Map<String, Object> result = (Map<String, Object>) operationMethod.invoke(null, node, workingMemory);

      return result;
   }

//   private Map<String, Object> getOperationInputs(Node node, Map<String, Object> workingMemory) {
//      Map<String, Object> results = new HashMap<>();
//      // query for the upstream thought_sequence edges
//      List<Triple<Node, Edge, Node>> triples = App.getGardenGraph().expandLeft(node, "testEdgeType", null, null);
//      
//      // process the edges 
//      for(Triple<Node, Edge, Node> triple : triples) {
//         Edge edge = triple.getMiddle();
//         String edgeInputAttrName = (String)edge.getAttribute("input");
//         String edgeOutputAttrName = (String)edge.getAttribute("output");
//         String edgeInputKey = (String)edge.getAttribute("_left");
//         Object operationInputValue = workingMemory.get(edgeInputKey + "." + edgeInputAttrName);
//         results.put(edgeOutputAttrName, operationInputValue);
//      }
//      return results;
//   }

   private Map<String, Object> addResultContext(Map<String, Object> workingMemory, Object opResult, String elementKey) {
      workingMemory.put(elementKey + ".result", opResult);
      return workingMemory;
   }

   private Map<String, Object> addContext(Map<String, Object> workingMemory, String propertyName, Object propertyValue, String elementKey) {
      workingMemory.put(elementKey + "." + propertyName, propertyValue);
      return workingMemory;
   }

   private Map<String, Object> addContext(Map<String, Object> workingMemory, Map<String, Object> propertyMap, String elementKey) {
      Set<String> keyset = propertyMap.keySet();
      for (String key : keyset) {
         workingMemory.put(elementKey + "." + key, propertyMap.get(key));
      }
      return workingMemory;
   }

   protected List<Set<Node>> getOperationsByMaxLayer() {
      // Note: see p.14 of LBB for details.
      Map<Integer, Set<String>> resultsMap = new HashMap<>();

      Integer currentLevel = 0;
      List<Node> startingPoints = new ArrayList<>();

      // The max distance from the start node for any path
      Map<String, Integer> nodeMaxLevel = new HashMap<>();
      nodeMaxLevel.put(_thoughtNode.getKey() + ":" + _thoughtNode.getType(), currentLevel);
      startingPoints.add(_thoughtNode);

      while (startingPoints.size() > 0) {
         List<Node> nextStartingPoints = new ArrayList<>();
         currentLevel += 1;
         for (Node startingPoint : startingPoints) {
            List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(startingPoint, "thought_sequence", null, null);
            for (Triple<Node, Edge, Node> expansion : expansions) {
               Node right = expansion.getRight();
               nextStartingPoints.add(right);
               nodeMaxLevel.put(right.getKey() + ":" + right.getType(), currentLevel);
            }
         }
         startingPoints = nextStartingPoints;
      }

      logger.debug("nodeMaxLevel = " + nodeMaxLevel);

      Iterator<String> maxLevelIterator = nodeMaxLevel.keySet().iterator();
      while (maxLevelIterator.hasNext()) {
         String current = maxLevelIterator.next();
         // String currentId = current.split(":")[0];
         Integer currentIdLevel = nodeMaxLevel.get(current);
         Set<String> nodeLevelcontents = resultsMap.get(currentIdLevel);

         if (null == nodeLevelcontents) {
            nodeLevelcontents = new HashSet<>();
         }
         nodeLevelcontents.add(current);
         resultsMap.put(currentIdLevel, nodeLevelcontents);
      }
      Integer numLayers = resultsMap.size();
      List<Set<Node>> results = new ArrayList<>();
      for (int i = 0; i < numLayers; i++) {
         Set<String> thisLayer = resultsMap.get(i);
         Set<Node> thisLayerNodes = new HashSet<>();
         for (String nodeString : thisLayer) {
            String nodeKey = nodeString.split(":")[0];
            String nodeType = nodeString.split(":")[1];
            thisLayerNodes.add(App.getGardenGraph().getNodeByKey(nodeKey, nodeType));
         }
         results.add(thisLayerNodes);
      }
      return results;
   }

   public String getKey() {
      return _thoughtNode.getKey();
   }
}
