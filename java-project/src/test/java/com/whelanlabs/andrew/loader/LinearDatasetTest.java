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
   public void load_datasetNotpreviouslyLoaded_loaded() throws Exception {
      app.getGraph().flush();
      app.loadDataset(new LinearDataset());
      Long graphCount = app.getGraph().getTotalCount();
      assert (graphCount > 0) : "graphCount = " + graphCount;
   }

   @Test
   public void load_datasetPreviouslyLoaded_loaded() throws Exception {
      app.getGraph().flush();
      app.loadDataset(new LinearDataset());
      Long graphCount1 = app.getGraph().getTotalCount();
      assert (graphCount1 > 0) : "graphCount1 = " + graphCount1;
      app.loadDataset(new LinearDataset());
      Long graphCount2 = app.getGraph().getTotalCount();
      assert (graphCount1 == graphCount2 ) : "{graphCount1, graphCount2} = " + graphCount1 + ", " + graphCount2;
   }

}
