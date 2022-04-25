package com.whelanlabs.andrew.loader;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public class LinearDatasetTest {

   private static App app;

   private static Logger logger = LogManager.getLogger(LinearDatasetTest.class);

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
   public void expand_datasetLoaded_getOtherSide() throws Exception {
      app.getGraph().flush();
      app.loadDataset(new LinearDataset());

      Node startingNode = app.getGraph().getNodeByKey("LinearDatasetNode_100", "LinearDatasetNode");
      List<Triple<Node, Edge, Node>> results1 = app.getGraph().expandRight(startingNode, "LinearDatasetEdge", null, null);

      assert (results1.size() == 1) : "results1 = " + results1;
      // test otherside
      logger.debug("results1 = " + results1);

      // test for time values
      assert (Integer.valueOf(100).equals(results1.get(0).getLeft().getAttribute("time")));
      assert (Integer.valueOf(101).equals(results1.get(0).getRight().getAttribute("time")));
      assert (Integer.valueOf(101).equals(results1.get(0).getMiddle().getAttribute("time")));

      Integer v1 = (Integer) results1.get(0).getLeft().getAttribute("value");
      Integer v2 = (Integer) results1.get(0).getRight().getAttribute("value");

      List<Triple<Node, Edge, Node>> results2 = app.getGraph().expandRight(results1.get(0).getRight(), "LinearDatasetEdge", null, null);
      // test otherside (again)
      assert (results2.size() == 1) : "results2 = " + results2;
      Integer v3 = (Integer) results2.get(0).getRight().getAttribute("value");
      // test for linear growth
      assert (v3 == v1 + (2 * (v2 - v1)));
   }
}
