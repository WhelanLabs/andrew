package com.whelanlabs.andrew;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Node;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      App.initialize("linear_dataset_tests");
      App.getDataGraph().flush();
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      App.getDataGraph().cleanup();
   }
   
    @Test
    public void getDataGraphConnection_valid_success() throws Exception
    {
       Long graphCount = App.getDataGraph().getTotalCount();
        assert( 0 == graphCount ): "graphCount = " + graphCount;
    }
    
    @Test
    public void getGarderGraphConnection_valid_success() throws Exception
    {

       Long graphCount = App.getGardenGraph().getTotalCount();
        assert( 0 == graphCount ): "graphCount = " + graphCount;
    }
    
    @Test
    public void load_datasetNotpreviouslyLoaded_loaded() throws Exception {
       App.getDataGraph().flush();
       App.loadDataset(new LinearDataset());
       Long graphCount = App.getDataGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount == 2000) : "graphCount = " + graphCount;
    }
    
    @Test
    public void load_datasetPreviouslyLoaded_loaded() throws Exception {
       App.getDataGraph().flush();
       App.loadDataset(new LinearDataset());
       Long graphCount1 = App.getDataGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount1 == 2000) : "graphCount1 = " + graphCount1;
       App.loadDataset(new LinearDataset());
       Long graphCount2 = App.getDataGraph().getTotalCount();

       // 1000 nodes, 999 edges, 1 dataSet_info node
       assert (graphCount1.equals(graphCount2)) : "{graphCount1, graphCount2} = " + graphCount1 + ", " + graphCount2;
    }
    
    @Test(expected = RuntimeException.class)
    public void load_doubleLoaded_exception() throws Exception {
       App.getDataGraph().flush();
       LinearDataset dataset = new LinearDataset();
       Node datasetInfoNode1 = new Node("firstNode", "dataSet_info");
       datasetInfoNode1.addAttribute("dataset_id", dataset.getDatasetInfoID());
       
       Node datasetInfoNode2 = new Node("secondNode", "dataSet_info");
       datasetInfoNode2.addAttribute("dataset_id", dataset.getDatasetInfoID());
       
       App.getDataGraph().upsert(datasetInfoNode1, datasetInfoNode2);
       App.loadDataset(dataset);
    }
}
