package com.whelanlabs.andrew.process.evaluate;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.Goal;
import com.whelanlabs.andrew.Thought;
import com.whelanlabs.andrew.dataset.LinearDataset;

public class EvaluatorTest {

   private static Logger logger = LogManager.getLogger(EvaluatorTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "andrew_test_database";
      App.initialize(databaseName);
      App.loadDatasetToDataGraph(new LinearDataset());
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }
   
   @Test(expected = IndexOutOfBoundsException.class)
   public void getActual_nullTargetTime_exception() throws Exception {
      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));
      Thought rootThought = App.loadThoughtFromJson("linear_growth_thought", content);
      Goal goal = rootThought.getGoal();
      goal.setProperty("targetDistance", 20);
      goal.setProperty("targetRel", "stockOnDate");
      goal.setProperty("otherSidePrefix", "stockSymbol/");
      
      Evaluator evaluator = new Evaluator(goal.getNode());
      evaluator.getActual(123l, "dummpValue");
   }
   
}
