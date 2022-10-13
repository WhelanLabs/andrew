package com.whelanlabs.andrew;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.andrew.process.evaluate.Evaluation;
import com.whelanlabs.andrew.process.evaluate.Evaluator;

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
   public void dummyTest() throws Exception {
      // for now, this class is needed...
   }
   
}
