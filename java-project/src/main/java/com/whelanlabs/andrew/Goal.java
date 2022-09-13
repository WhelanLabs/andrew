package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

}
