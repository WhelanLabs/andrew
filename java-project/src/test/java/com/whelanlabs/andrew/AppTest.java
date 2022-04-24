package com.whelanlabs.andrew;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.loader.LinearDataset;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

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
    public void getKGraphConnection_valid_success() throws Exception
    {
       String databaseName = "andrew_test_database";
       App app = new App(databaseName);
       Long graphCount = app.getGraph().getTotalCount();
        assert( 0 == graphCount ): "graphCount = " + graphCount;
    }
    
    @Test
    public void load_datasetNotpreviouslyLoaded_loaded() throws Exception {
       app.getGraph().flush();
       app.loadDataset(new LinearDataset());
       Long graphCount = app.getGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount == 2000) : "graphCount = " + graphCount;
    }

    @Test
    public void load_datasetPreviouslyLoaded_loaded() throws Exception {
       app.getGraph().flush();
       app.loadDataset(new LinearDataset());
       Long graphCount1 = app.getGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount1 == 2000) : "graphCount1 = " + graphCount1;
       app.loadDataset(new LinearDataset());
       Long graphCount2 = app.getGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount1.equals(graphCount2)) : "{graphCount1, graphCount2} = " + graphCount1 + ", " + graphCount2;
    }
    
    @Test(expected = RuntimeException.class)
    public void load_doubleLoaded_exception() throws Exception {
       app.getGraph().flush();
       LinearDataset dataset = new LinearDataset();
       Node datasetInfoNode1 = new Node("firstNode", "dataSet_info");
       datasetInfoNode1.addAttribute("dataset_id", dataset.getDatasetInfoID());
       
       Node datasetInfoNode2 = new Node("secondNode", "dataSet_info");
       datasetInfoNode2.addAttribute("dataset_id", dataset.getDatasetInfoID());
       
       app.getGraph().upsert(datasetInfoNode1, datasetInfoNode2);
       app.loadDataset(dataset);
    }
}
