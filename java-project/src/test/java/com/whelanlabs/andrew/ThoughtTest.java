package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
      Thought thought = new Thought();
      Node startingPoint = new Node(ElementHelper.generateKey(), ElementHelper.generateName());
      startingPoint.addAttribute("value", Float.valueOf("3.14159"));
      long goalDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC); 
      Goal goal = new Goal(goalDate);
      Node result = thought.forecast(startingPoint, goal);
      logger.debug("result = " + result);
      assert(result != null);
      Long resultTime = (Long)result.getAttribute("time");
      assert( goalDate == resultTime ) : "result = " + result;
      Object value = result.getAttribute("value");
      assert(value instanceof Float) : "value class: " + value.getClass().getName();
   }
   
}
