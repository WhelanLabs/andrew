package com.whelanlabs.andrew;

import com.whelanlabs.kgraph.engine.Node;

public class Evaluation {

   private Node _thought;
   private Number _guess;
   private Number _actual;

   public Evaluation(Node thought, Number guess, Number actual) {
      _thought = thought;
      _guess = guess;
      _actual = actual;
   }
   
   public Number getGuess() {
      return _guess;
   }
   
   public Number getActual() {
      return _actual;
   }
   
   public Node getThought() {
      return _thought;
   }
}
