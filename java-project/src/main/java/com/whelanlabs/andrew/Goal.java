package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public class Goal {

   private Node _goal;

   public Goal(Node goal) {
      _goal = goal;
   }

   public Node getNode() {
      return _goal;
   }

   public List<Thought> getThoughts() {
      List<Thought> results = new ArrayList<>();
      List<Triple<Node, Edge, Node>> triples = App.getGardenGraph().expandRight(_goal, "approach", null, null);
      for(Triple<Node, Edge, Node> triple : triples) {
         Thought thought = new Thought(triple.getRight());
         results.add(thought);
      }

      return results;
   }

   public Map<String, Object> setTrainingParameters(Map<String, List<Object>> trainingParameters) {
      Map<String, Object> workingMemory = new HashMap<>();
      for(String key : trainingParameters.keySet() ) {
         List<Object> values = trainingParameters.get(key);
         Collections.shuffle(values);
         workingMemory.put("GOAL." + key, values.get(0));
      }
      return workingMemory;
   }

}
