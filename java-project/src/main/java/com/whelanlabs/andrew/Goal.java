package com.whelanlabs.andrew;

import java.util.ArrayList;
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

   public Map<String, Object> setTrainingParameters(TrainingParameters trainingParameters) {
      Map<String, Object> workingMemory = new HashMap<>();
      for(String key : trainingParameters.getGoalAttributes() ) {
         Object value = trainingParameters.getRandomValue(key);
         workingMemory.put("GOAL." + key, value);
         _goal.addAttribute(key, value);
      }
      return workingMemory;
   }

   public void setProperty(String key, Object value) {
      _goal.addAttribute(key, value);     
   }

}
