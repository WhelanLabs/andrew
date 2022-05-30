package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;

public class Evaluator {

   private Node _goal;

   public Evaluator(Node goal) {
      _goal = goal;
   }

   public List<Evaluation> evaluateThoughts(Integer maxTime, Integer numTests) throws Exception {
      List<Evaluation> results = new ArrayList<>();

      List<Triple<Node, Edge, Node>> expansions = App.getDataGraph().expandRight(_goal, "approach", null, null);

      List<Node> thoughts = expansions.stream().map(object -> object.getRight()).collect(Collectors.toList());

      Random random = new Random();

      for (int i = 0; i < numTests; i++) {
         Integer randomNumber = random.nextInt(maxTime);

         // TODO: push this down into KGraph.

         /* TODO: use AQL to sort and limit based on the closest time
          * before or equal to the limit.
          */
         try {
            // TODO: Should the DB be exposed? code change needed?
            KnowledgeGraph arangoDB = App.getDataGraph();
            Object database = arangoDB.db("dbName");

            String query = "FOR t IN firstCollection FILTER t.name == @name RETURN t";
            Map<String, Object> bindVars = Collections.singletonMap("name", "Homer");
            ArangoCursor<BaseDocument> cursor = database.query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
               System.out.println("Key: " + aDocument.getKey());
            });
         } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
         }

         Node startingNode = null;

         for (Node thoughtNode : thoughts) {
            Thought thought = new Thought(thoughtNode);
            thought.forecast(startingNode);
         }
      }

      return null;
   }

}
