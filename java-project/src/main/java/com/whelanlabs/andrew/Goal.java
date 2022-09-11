package com.whelanlabs.andrew;

import com.whelanlabs.kgraph.engine.Node;

public class Goal {

   private Node _goal;

   public Goal(Node goal) {
      _goal = goal;
   }

   public Node getNode() {
      return _goal;
   }

}
