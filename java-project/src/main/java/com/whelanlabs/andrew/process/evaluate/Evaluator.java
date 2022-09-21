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
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public class Evaluator {

   private Node _goal;
   private static Logger logger = LogManager.getLogger(Evaluator.class);

   public Evaluator(Node goal) {
      _goal = goal;
   }

   public List<Evaluation> evaluateThoughts2(Long minTime, Long maxTime, Integer numTests, Map<String, Object> initialWorkingMemory) throws Exception {
      Random random = new Random();
      
      List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(_goal, "approach", null, null);
      logger.debug("expansions: " + expansions);

      List<Node> thoughts = expansions.stream().map(object -> object.getRight()).collect(Collectors.toList());
      for (int i = 0; i < numTests; i++) {
         Long randomTime = random.nextLong(maxTime-minTime) + minTime;
         Number forecastResult = null;
         
         for (Node thoughtNode : thoughts) {
            Thought thought = new Thought(thoughtNode);
            Map<String, Object> workingMemory = new HashMap<>();
            workingMemory = thought.addContext(initialWorkingMemory, "startDate", randomTime, "GOAL");
            Map<String, Object> forecastOutput = thought.forecast2(workingMemory);
            forecastResult = (Number) forecastOutput.get("RESULT.output");
            logger.debug("forecastResult = " + forecastResult);
         }
      }
      
      throw new RuntimeException("not yet implemented");
      // return null;
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
         Long randomTime = random.nextLong(maxTime-minTime) + minTime;
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
            results.add(new Evaluation(thoughtNode, forecastResult, actualResult));
         }
      }
      return results;
   }



}
