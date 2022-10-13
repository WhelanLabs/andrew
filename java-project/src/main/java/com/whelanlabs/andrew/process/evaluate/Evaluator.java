package com.whelanlabs.andrew.process.evaluate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.Operations;
import com.whelanlabs.andrew.Thought;
import com.whelanlabs.andrew.TrainingCriteria;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;
import com.whelanlabs.kgraph.engine.QueryClause.Operator;

public class Evaluator {

   private static Integer numEvalFailures = 0;
   private static Integer numEval = 0;

   private Node _goal;
   private static Logger logger = LogManager.getLogger(Evaluator.class);

   public Evaluator(Node goal) {
      _goal = goal;
   }

   public List<Evaluation> evaluateThoughts2(TrainingCriteria trainingCriteria, Map<String, Object> initialWorkingMemory) throws Exception {

      Long minTime = trainingCriteria.getStartDateLong();
      Long maxTime = trainingCriteria.getEndDateLong();
      Integer numTests = trainingCriteria.getQuestsPerGeneration();

      List<Evaluation> results = new ArrayList<>();

      Random random = new Random();

      List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(_goal, "approach", null, null);
      logger.debug("expansions: " + expansions);

      List<Node> thoughts = expansions.stream().map(object -> object.getRight()).collect(Collectors.toList());

      String otherSidePrefix = (String) _goal.getAttribute("otherSidePrefix");

      for (int i = 0; i < numTests; i++) {
         Long randomTime = random.nextLong(maxTime - minTime) + minTime;
         Number forecastResult = null;

         for (Node thoughtNode : thoughts) {
            numEval++;
            try {
               Thought thought = new Thought(thoughtNode);
               Map<String, Object> workingMemory = clone(initialWorkingMemory);
               workingMemory = thought.addContext(workingMemory, "startDate", randomTime, "GOAL");
               logger.debug("workingMemory before forecast2 = " + workingMemory);
               Map<String, Object> forecastOutput = thought.forecast2(workingMemory);
               logger.debug("workingMemory after forecast2 = " + workingMemory);
               forecastResult = (Number) forecastOutput.get("RESULT.output");
               logger.debug("randomTime = " + randomTime + ",       forecastResult = " + forecastResult);

               // TODO: fix this hack - should be a generic attribute
               String otherSideID = (String) workingMemory.get("GOAL.symbol");
               Number actual = getActual(randomTime, otherSidePrefix + otherSideID);
               results.add(new Evaluation(thoughtNode, forecastResult, actual, workingMemory));
            } catch (Exception e) {
               numEvalFailures++;
               logger.error("forecast2 failed.  (failure rate = " + numEvalFailures + "/" + numEval + ")", e);
               forecastResult = null;
            }
         }
      }

      return results;
   }

   private Number getActual(Long startingTime, String otherSideID) {
      // String targetType = (String) _goal.getAttribute("targetType");
      Integer distance = (Integer) _goal.getAttribute("targetDistance");
      String targetProperty = (String) _goal.getAttribute("targetProperty");
      String relType = (String) _goal.getAttribute("targetRel");
      Long targetTime = startingTime + distance;

      String query = "FOR t IN date FILTER t.dateNumber <= @time SORT t.dateNumber DESC LIMIT 1 RETURN t";
      logger.debug("query: " + query);
      Map<String, Object> bindVars = Collections.singletonMap("time", targetTime);
      logger.debug("bindVars: " + bindVars);
      List<Node> queryResults = null;
      Node dateNode = null;
      try {
         queryResults = App.getDataGraph().queryNodes(query, bindVars);
         dateNode = queryResults.get(0);
      } catch (Exception e) {
         logger.error("no date results");
         logger.error("query: " + query);
         logger.error("bindVars: " + bindVars);
         throw e;
      }

      List<Triple<Node, Edge, Node>> expansions = null;
      Edge targetEdge = null;
      Number actualResult = null;
      try {
         // traverse from the date through the target rel
         // TODO: support reverse direction traversals.
         List<QueryClause> relClauses = new ArrayList<>();
         // stockSymbol/AAPL
         relClauses.add(new QueryClause("_from", Operator.EQUALS, otherSideID));
         expansions = App.getDataGraph().expandLeft(dateNode, relType, relClauses, null);

         // fixmeHere;

         // get the target object
         targetEdge = expansions.get(0).getMiddle();

         // get the target attribute
         actualResult = (Number) targetEdge.getAttribute(targetProperty);
      } catch (Exception e) {
         logger.error("no target object results");
         logger.error("dateNode: " + dateNode);
         logger.error("relType: " + relType);
         logger.error("targetObject: " + targetEdge);
         throw e;
      }

      return actualResult;
   }

   public static <K, V> Map<K, V> clone(Map<K, V> original) {
      Map<K, V> copy = new HashMap<>();

      for (Map.Entry<K, V> entry : original.entrySet()) {
         copy.put(entry.getKey(), entry.getValue());
      }

      return copy;
   }

   public List<Evaluation> evaluateThoughts(Long minTime, Long maxTime, Integer numTests) throws Exception {
      logger.debug("evaluateThoughts: " + minTime + ", " + maxTime + ", " + numTests);

      List<Node> startingNodes = new ArrayList<>();

      List<Evaluation> results = new ArrayList<>();

      List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(_goal, "approach", null, null);
      logger.debug("expansions: " + expansions);

      List<Node> thoughts = expansions.stream().map(object -> object.getRight()).collect(Collectors.toList());

      Random random = new Random();

      for (int i = 0; i < numTests; i++) {
         Long randomTime = random.nextLong(maxTime - minTime) + minTime;
         logger.debug("randomTime: " + randomTime);

         String startingType = (String) _goal.getAttribute("startingType");
         logger.debug("_goal: " + _goal);
         logger.debug("startingType: " + startingType);

         String query = "FOR t IN " + startingType + " FILTER t.time <= @time SORT t.time DESC LIMIT 1 RETURN t";
         logger.debug("query: " + query);

         Map<String, Object> bindVars = Collections.singletonMap("time", randomTime);

         startingNodes.addAll(App.getDataGraph().queryNodes(query, bindVars));
      }

      logger.debug("startingNodes: " + startingNodes);
      for (Node startingNode : startingNodes) {

         String direction = (String) _goal.getAttribute("direction");
         String relationType = (String) _goal.getAttribute("relationType");
         Integer distance = (Integer) _goal.getAttribute("distance");
         String targetProperty = (String) _goal.getAttribute("targetProperty");
         Number actualResult = (Number) Operations.traverse(startingNode, direction, relationType, distance).getAttribute(targetProperty);

         for (Node thoughtNode : thoughts) {
            Thought thought = new Thought(thoughtNode);
            Number forecastResult = null;
            try {
               Map<String, Object> workingMemory1 = new HashMap<>();
               String targetPropName = (String) thought.getGoalNode().getAttribute("targetProperty");
               Object startingTargetPropValue = startingNode.getAttribute(targetPropName);
               workingMemory1 = thought.addContext(workingMemory1, "startingNode", startingNode, "GOAL");
               workingMemory1 = thought.addContext(workingMemory1, startingNode.getProperties(), startingNode.getKey());
               workingMemory1 = thought.addContext(workingMemory1, thought.getGoalNode().getProperties(), "GOAL");
               workingMemory1 = thought.addContext(workingMemory1, "targetPropValue", startingTargetPropValue, thought.getThoughtNode().getKey());
               workingMemory1 = thought.addContext(workingMemory1, "distance", distance, thought.getThoughtNode().getKey());
               workingMemory1 = thought.addContext(workingMemory1, "direction", direction, thought.getThoughtNode().getKey());
               workingMemory1 = thought.addContext(workingMemory1, "startingNode", startingNode, thought.getThoughtNode().getKey());
               workingMemory1 = thought.addContext(workingMemory1, "relationType", relationType, thought.getThoughtNode().getKey());

               Map<String, Object> forecastOutput = thought.forecast2(workingMemory1);
               forecastResult = (Number) forecastOutput.get("RESULT.output");
            } catch (Exception e) {
               logger.warn("Forecast failed: " + e.getMessage() + "\n" + "startingNode = " + startingNode + "\n" + "thoughtNode = " + thoughtNode);
            }
            results.add(new Evaluation(thoughtNode, forecastResult, actualResult, null));
         }
      }
      return results;
   }

}
