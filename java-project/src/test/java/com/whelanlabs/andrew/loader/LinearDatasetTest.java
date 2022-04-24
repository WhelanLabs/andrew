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
      App app = new App("linear_dataset_tests");
      app.getGraph().flush();
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      app.getGraph().cleanup();
   }

   @Test
   public void load_datasetNotLoaded_loaded() throws Exception {
      app.getGraph().flush();
      app.loadDataset(new LinearDataset());
      Long graphCount = app.getGraph().getTotalCount();
      assert (graphCount > 0) : "graphCount = " + graphCount;
      fail("Not yet implemented");
   }

   @Test
   public void load_datasetLoaded_loaded() throws Exception {
      app.getGraph().flush();
      app.loadDataset(new LinearDataset());
      app.loadDataset(new LinearDataset());
      Long graphCount = app.getGraph().getTotalCount();
      assert (graphCount > 0) : "graphCount = " + graphCount;
      fail("Not yet implemented");
      fail("Not yet implemented");
   }

}
