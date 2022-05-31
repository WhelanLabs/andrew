package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public class Evaluator {

   private Node _goal;
   private static Logger logger = LogManager.getLogger(Evaluator.class);

   public Evaluator(Node goal) {
      _goal = goal;
   }

   public List<Evaluation> evaluateThoughts(Integer minTime, Integer maxTime, Integer numTests) throws Exception {
      logger.debug("evaluateThoughts: " + minTime + ", " + maxTime + ", " + numTests);

      List<Node> startingNodes = new ArrayList<>();

      List<Evaluation> results = new ArrayList<>();

      List<Triple<Node, Edge, Node>> expansions = App.getGardenGraph().expandRight(_goal, "approach", null, null);
      logger.debug("expansions: " + expansions);

      List<Node> thoughts = expansions.stream().map(object -> object.getRight()).collect(Collectors.toList());

      Random random = new Random();

      for (int i = 0; i < numTests; i++) {
         Integer randomTime = random.nextInt(maxTime-minTime) + minTime;
         logger.debug("randomTime: " + randomTime);

         // TODO: port the following code to KGraph once it is working.

         /* TODO: use AQL to sort and limit based on the closest time
          * before or equal to the limit.
          */
            // startingType
            String startingType = (String) _goal.getAttribute("startingType");
            logger.debug("_goal: " + _goal);
            logger.debug("startingType: " + startingType);

            ArangoDatabase arangoUserDB = App.getDataGraph()._userDB;

            String query = "FOR t IN " + startingType + " FILTER t.time <= @time SORT t.time DESC LIMIT 1 RETURN t";
            logger.debug("query: " + query);

            Map<String, Object> bindVars = Collections.singletonMap("time", randomTime);
            ArangoCursor<Node> cursor = arangoUserDB.query(query, bindVars, null, Node.class);
            cursor.forEachRemaining(aDocument -> {
               logger.debug("result: " + aDocument);
               startingNodes.add(aDocument);
            });
      }

      logger.debug("startingNodes: " + startingNodes);
      for (Node startingNode : startingNodes) {

         // TODO: traverse to the actual result
         // Operations.traverse(Node startingNode, String direction, String relationType,
         // Integer distance);
         String direction = (String) _goal.getAttribute("direction");
         String relationType = (String) _goal.getAttribute("relationType");
         Integer distance = (Integer) _goal.getAttribute("distance");
         String targetProperty = (String) _goal.getAttribute("targetProperty");
         Number actualResult = (Number) Operations.traverse(startingNode, direction, relationType, distance).getAttribute(targetProperty);

         for (Node thoughtNode : thoughts) {
            Thought thought = new Thought(thoughtNode);
            Number forecastResult = null;
            try {
               forecastResult = (Number) thought.forecast(startingNode).get("RESULT.output");
            } catch (Exception e) {
               logger.warn("Forecast failed: " + e.getMessage() + "\n" + "startingNode = " + startingNode + "\n" + "thoughtNode = " + thoughtNode);
            }
            results.add(new Evaluation(thoughtNode, forecastResult, actualResult));
         }
      }
      return results;
   }

}
