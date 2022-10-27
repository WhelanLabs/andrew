package com.whelanlabs.andrew.process;

import java.util.Map;

import com.whelanlabs.kgraph.engine.Node;

public class Evaluation {

   private Node _thought;
   private Number _guess;
   private Number _actual;
   private Map<String, Object> _workingMemory;

   public Evaluation(Node thought, Number guess, Number actual, Map<String, Object> workingMemory) {
      _thought = thought;
      _guess = guess;
      _actual = actual;
      _workingMemory = workingMemory;
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
   
   public  Map<String, Object> getWorkingMemory() {
      return _workingMemory;
   }
   
   public String toString(){
      String result =  "   { " + 
         "guess\" : " + getGuess() +
         "\", actual\" : " + getActual() +
         "\", thoughtKey\" : " + getThought().getKey() +
         "\" ";
      for ( String memoryKey : _workingMemory.keySet()) {
         if(memoryKey.startsWith("GOAL.")) {
            result += ", \"" + memoryKey + "\" : \"" + _workingMemory.get(memoryKey) + "\" ";
         }
      }
         
      result +=  "}\n";
      return result;
     }
}
