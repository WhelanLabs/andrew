package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

public class EvaluatorTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void evaluate_perfectFit_one() {
      Evaluator evaluator = new Evaluator();
      Goal goal = new Goal("next", Direction.outbound, 1, "value");
      String nodeType = ElementHelper.generateName();
      Node guess = new Node(ElementHelper.generateKey(), nodeType);
      guess.addAttribute("value", Float.valueOf("3.14159"));
      Node answer = new Node(ElementHelper.generateKey(), nodeType);
      answer.addAttribute("value", Float.valueOf("3.14159"));
      Criteria criteria = new Criteria();
      Float fitness = evaluator.evualate(guess, answer, goal, criteria );
      
      assert(fitness != null);
      float tolerableDifference = (float) 0.00001;
      assert(Math.abs(fitness -1) < tolerableDifference);
   }

}
