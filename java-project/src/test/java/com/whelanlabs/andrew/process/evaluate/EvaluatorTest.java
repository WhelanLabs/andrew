package com.whelanlabs.andrew.process.evaluate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.Goal;
import com.whelanlabs.andrew.Thought;
import com.whelanlabs.andrew.dataset.CSVLoader;
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
   
   @Test
   public void getActual_nullTargetTime_exception() throws Exception {
      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));
      Thought rootThought = App.loadThoughtFromJson("linear_growth_thought", content);
      Goal goal = rootThought.getGoal();
      goal.setProperty("targetDistance", 20);
      goal.setProperty("targetRel", "stockOnDate");
      goal.setProperty("otherSidePrefix", "stockSymbol/");
      
      Evaluator evaluator = new Evaluator(goal.getNode());
      boolean exceptionThrown = false;
      try {
         evaluator.getActual(123l, "dummpValue");
      }
      catch (RuntimeException e) {
         if("no date results".equals(e.getMessage())) {
            exceptionThrown = true;
         }
      }
      assert(exceptionThrown);
      
   }
   
   
   @Test //(expected = IndexOutOfBoundsException.class)
   public void getActual_badTargetProperty_exception() throws Exception {
      List<File> files = new ArrayList<>();
      List<String> tickers = new ArrayList<>();
      String baseDir = "../fetchers/stock_data_fetcher/data/";
      File f = new File(baseDir);
      String[] baseFileNames = f.list();
      for (String baseFileName : baseFileNames) {
         if(baseFileName.startsWith("AMAT_")) {
            String filePath = baseDir + baseFileName;
            String ticker = baseFileName.substring(0, baseFileName.indexOf("_")-1);
            tickers.add(ticker);
            files.add(new File(filePath));
            logger.debug("adding ticker " + ticker + "(" + baseFileName + ")");
         }
      }
      CSVLoader stockLoader = new CSVLoader();
      stockLoader.loadStocks(files);
      
      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));
      Thought rootThought = App.loadThoughtFromJson("linear_growth_thought", content);
      Goal goal = rootThought.getGoal();
      goal.setProperty("targetDistance", 20);
      goal.setProperty("targetRel", "stockOnDate");
      goal.setProperty("otherSidePrefix", "stockSymbol/");
      goal.setProperty("targetProperty", null);

      Evaluator evaluator = new Evaluator(goal.getNode());
      
      boolean exceptionThrown = false;
      try {
         evaluator.getActual(5000l, "dummpValue");
      }
      catch (RuntimeException e) {
         if("no target object results".equals(e.getMessage())) {
            exceptionThrown = true;
         }
      }
      assert(exceptionThrown);
   }
}
