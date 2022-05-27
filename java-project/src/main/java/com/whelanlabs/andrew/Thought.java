package com.whelanlabs.andrew;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
   public Map<String, Object> forecast(Node startingPoint) throws Exception {
      logger.debug("forecast startingPoint = " + startingPoint);
      logger.debug("_thoughtSequences = " + _thoughtSequences);

      Map<String, Object> workingMemory = new HashMap<>();

      Map<String, Object> result = new HashMap<>();

      // get the initial layer inputs from the goal
      workingMemory = addContext(workingMemory, startingPoint.getProperties(), startingPoint.getKey());
      workingMemory = addContext(workingMemory, _goal.getProperties(), "GOAL");

      workingMemory = addContext(workingMemory, "startingNode", startingPoint, "GOAL");

      List<Set<Node>> layeredOperations = getOperationsByMaxLayer();

      // process the thought by layer...
      for (Set<Node> currentOperations : layeredOperations) {

         // process the nodes of a layer
         logger.debug("layer contents = " + currentOperations);
         Set<String> nextLevelInputNodeKeys = new HashSet<>();
         for (Node node : currentOperations) {
            logger.debug("add nextLevelInputNodeKeys: " + node);
            nextLevelInputNodeKeys.add(node.getKey());

            if ("thought_operation".equals(node.getType())) {

               // process the operation
               Map<String, Object> opResult = processOperation(node, workingMemory);

               // add the result of the operation to working memory
               workingMemory = addContext(workingMemory, opResult, node.getKey());

            } else if ("thought".equals(node.getType())) {
               logger.debug("thought node = " + node);

               // have the thought consume some goal details
               List<Triple<Node, Edge, Node>> goalTriples = App.getGardenGraph().expandLeft(node, "approach", null, null);
               Node goal = goalTriples.get(0).getRight();
               logger.debug("goal node = " + goal);

               String targetPropName = (String) goal.getAttribute("targetProperty");
               logger.debug("targetPropName = " + targetPropName);
               Object startingTargetPropValue = startingPoint.getAttribute(targetPropName);
               workingMemory = addContext(workingMemory, "targetPropValue", startingTargetPropValue, node.getKey());

               // Node startingNode = (Node) goal.getAttribute("startingNode");
               workingMemory = addContext(workingMemory, "startingNode", startingPoint, node.getKey());

               String direction = (String) goal.getAttribute("direction");
               workingMemory = addContext(workingMemory, "direction", direction, node.getKey());

               String relationType = (String) goal.getAttribute("relationType");
               workingMemory = addContext(workingMemory, "relationType", relationType, node.getKey());

               Integer distance = (Integer) goal.getAttribute("distance");
               workingMemory = addContext(workingMemory, "distance", distance, node.getKey());
            } else if ("thought_result".equals(node.getType())) {
               Map<String, Object> opResult = processOperation(node, workingMemory);
               result = addResultContext(result, opResult, node.getKey());
               return result;
            }

            // use the tailing edges to add next-level inputs to working memory
            for (String nextLevelInputNodeKey : nextLevelInputNodeKeys) {
               logger.debug("generating inputs based on outputs from " + nextLevelInputNodeKey + ".");
               QueryClause queryClause = new QueryClause("_from", QueryClause.Operator.EQUALS, node.getId());
               logger.debug("inputEdges queryClause name and value: " + queryClause.getName() + ", " + queryClause.getValue());
               List<Edge> inputEdges = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
               logger.debug("next level input edges: " + inputEdges);
               for (Edge inputEdge : inputEdges) {
                  String inputProp = (String) inputEdge.getAttribute("input");
                  String edgeName = (String) inputEdge.getAttribute("name");
                  logger.debug("edgeName = " + edgeName);
                  String outputProp = (String) inputEdge.getAttribute("output");
                  String fromKey = inputEdge.getFrom().split("/")[1];
                  Object value = getInputValue(workingMemory, fromKey, inputProp);
                  // Object value = workingMemory.get(fromKey + "." + inputProp);
                  String toKey = (String) inputEdge.getTo().split("/")[1];
                  logger.debug("copying value '" + value + "': " + fromKey + "." + inputProp + " -> " + toKey + "." + outputProp);
                  workingMemory.put(toKey + "." + outputProp, value);
               }
            }
         }
      }

      return result;
   }

   private Object getInputValue(Map<String, Object> workingMemory, String fromKey, String inputProp) {
      logger.debug("getInputValue inputProp = " + inputProp);
      Object result = null;

      if (inputProp.startsWith("NUMBER.")) {
         String[] numStringArray = inputProp.split("\\.");
         String numString = numStringArray[1];
         result = Float.valueOf(numString);
      }
//      else if(inputProp.startsWith("GOAL.") ) {
//         result = workingMemory.get("GOAL." + inputProp);
//         logger.debug("### result = " + result);
//      }
      else {
         result = workingMemory.get(fromKey + "." + inputProp);
      }
      return result;
   }

   private Map<String, Object> processOperation(Node currentNode, Map<String, Object> workingMemory) throws Exception {
      String operationName = (String) currentNode.getAttribute("operationName");
      logger.debug("processOperation for Node Name: " + currentNode.getAttribute("name"));
      logger.debug("operation Name = " + operationName);
      logger.debug("workingMemory = " + workingMemory);

      // reflection to call the method with inputs.
      Method operationMethod = Operations.class.getMethod(operationName, Node.class, Map.class);

      Map<String, Object> result = (Map<String, Object>) operationMethod.invoke(null, currentNode, workingMemory);

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

   private Map<String, Object> addResultContext(Map<String, Object> workingMemory, Map<String, Object> propertyMap, String elementKey) {
      Set<String> keyset = propertyMap.keySet();
      for (String key : keyset) {
         Object value = propertyMap.get(key);
         if (value instanceof Node) {
            Map<String, Object> valueProps = ((Node) value).getProperties();
            Set<String> valuePropsKeyset = valueProps.keySet();
            for (String valueKey : valuePropsKeyset) {
               String resultKey = (key + "." + valueKey).replace(elementKey, "RESULT");
               workingMemory = addContext(workingMemory, resultKey, valueProps.get(valueKey), "RESULT");
            }
         } else {
            String resultKey = key.replace(elementKey, "RESULT");
            logger.debug("adding result to working memory: " + resultKey + " =  " + value);
            workingMemory.put(resultKey, value);
         }
      }
      return workingMemory;
   }

   private Map<String, Object> addContext(Map<String, Object> workingMemory, String propertyName, Object propertyValue, String elementKey) {
      String varName = elementKey + "." + propertyName;
      logger.debug("adding to working memory: " + varName + " =  " + propertyValue);
      workingMemory.put(varName, propertyValue);
      return workingMemory;
   }

   private Map<String, Object> addContext(Map<String, Object> workingMemory, Map<String, Object> propertyMap, String elementKey) {
      Set<String> keyset = propertyMap.keySet();
      String varName = null;
      for (String key : keyset) {
         varName = elementKey + "." + key;
         Object value = propertyMap.get(key);
         if (value instanceof Node) {
            Map<String, Object> valueProps = ((Node) value).getProperties();
            Set<String> valuePropsKeyset = valueProps.keySet();
            for (String valueKey : valuePropsKeyset) {
               workingMemory = addContext(workingMemory, key + "." + valueKey, valueProps.get(valueKey), elementKey);
            }
         } else {
            logger.debug("adding to working memory: " + varName + " =  " + value);
            workingMemory.put(varName, value);
         }
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

      // logger.debug("nodeMaxLevel = " + nodeMaxLevel);

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
