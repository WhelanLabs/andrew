package com.whelanlabs.andrew;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.dataset.LinearDataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
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
       App.getDataGraph().flush();
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
    
    @Test
    public void buildThought_valid_success() throws Exception {
       buildThought();
       assert(false); // replace with well-formed check
    }
    
    public void buildThought() {
       // Note: see thought_process_language.html for reference
       
       // create a thought node
       final Node thought = new Node("firstTestThought", "thought");
       
       // create steps     
       // TODO: make these reusable nodes with backing code.  how?
       final Node A1_getNumberAttrinbute = new Node(ElementHelper.generateKey(), "thought_step");
       final Node B1_traverse = new Node(ElementHelper.generateKey(), "thought_step");
       final Node B2_getNumberAttrinbute = new Node(ElementHelper.generateKey(), "thought_step");
       final Node A2_valueX2 = new Node(ElementHelper.generateKey(), "thought_step");
       final Node AB1_subtract = new Node(ElementHelper.generateKey(), "thought_step");
       
       final Node end = new Node(ElementHelper.generateKey(), "thought_result");
       
       // link the thought process using valid sequence relationships
       Edge edge1 = new Edge(ElementHelper.generateKey(), thought, A1_getNumberAttrinbute, "thought_sequence");
       edge1.addAttribute("thought_key", thought.getKey());
       Edge edge2 = new Edge(ElementHelper.generateKey(), A1_getNumberAttrinbute, A2_valueX2, "thought_sequence");
       edge2.addAttribute("thought_key", thought.getKey());
       Edge edge3 = new Edge(ElementHelper.generateKey(), A2_valueX2, AB1_subtract, "thought_sequence");
       edge3.addAttribute("thought_key", thought.getKey());
       Edge edge4 = new Edge(ElementHelper.generateKey(), thought, B1_traverse, "thought_sequence");
       edge4.addAttribute("thought_key", thought.getKey());
       Edge edge5 = new Edge(ElementHelper.generateKey(), B1_traverse, B2_getNumberAttrinbute, "thought_sequence");
       edge5.addAttribute("thought_key", thought.getKey());
       Edge edge6 = new Edge(ElementHelper.generateKey(), B2_getNumberAttrinbute, AB1_subtract, "thought_sequence");
       edge6.addAttribute("thought_key", thought.getKey());
       Edge edge7 = new Edge(ElementHelper.generateKey(), AB1_subtract, end, "thought_sequence");
       edge7.addAttribute("thought_key", thought.getKey());
    }
}
