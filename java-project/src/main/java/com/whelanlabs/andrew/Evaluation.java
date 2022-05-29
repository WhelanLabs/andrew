package com.whelanlabs.andrew;

import com.whelanlabs.kgraph.engine.Node;

public class Evaluation {

   private Node _thought;
   private Float _score;

   public Evaluation(Node thought, Float score) {
      _thought = thought;
      _score = score;
   }
   
   public Float getScore() {
      return _score;
   }
   
   public Node getThought() {
      return _thought;
   }
}
