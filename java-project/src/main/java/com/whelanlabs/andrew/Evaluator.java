package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public class Evaluator {
   
   private Node _goal;

   public Evaluator(Node goal) {
      _goal = goal;
   }

   public List<Evaluation> evaluateThoughts(TestRange testRange, Integer numTests) {
      List<Evaluation> results = new ArrayList<>();
      
      List<Triple<Node, Edge, Node>> expansions = App.getDataGraph().expandRight(_goal, "approach", null, null);
      
      List<Node> thoughts = expansions.stream()
            .map(object -> object.getRight() )
            .collect(Collectors.toList());
      
      for (int i=0; i<numTests; i++) {
         for (Node thought : thoughts) {
            
         }
      }

      return null;
   }
   
}
