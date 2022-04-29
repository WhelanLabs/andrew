package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

import org.apache.logging.log4j.Logger;

public class ThoughtTest {

   private static Logger logger = LogManager.getLogger(ThoughtTest.class);
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void test() {
      assert(true);
   }

   @Test
   public void forecast_simpleInputs_simpleOutputs() {
      // forecast given a simple thought and simple data. 
      
      String rel = "next";
      Direction direction = Direction.outbound;
      Integer distance = 1;
      String targetProperty = "value";
      
      Thought thought = new Thought();
      Node startingPoint = new Node(ElementHelper.generateKey(), ElementHelper.generateName());
      startingPoint.addAttribute(targetProperty, Float.valueOf("3.14159"));
      
      Goal goal = new Goal(rel, direction, distance, targetProperty);
      Node result = thought.forecast(startingPoint, goal);
      logger.debug("result = " + result);
      
      assert(result != null);
   }
   
}
