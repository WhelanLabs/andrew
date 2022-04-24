package com.whelanlabs.andrew.loader;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;

public class LinearDatasetTest {

   private static App app;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      app = new App("linear_dataset_tests");
      app.getGraph().flush();
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      app.getGraph().cleanup();
   }

   @Test
   public void rigorous_test() throws Exception {
      // having no tests in a test class makes for problems.
      assert(true);
   }

}
