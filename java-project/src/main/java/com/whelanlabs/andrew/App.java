package com.whelanlabs.andrew;

import java.util.List;

import com.whelanlabs.andrew.loader.LinearDataset;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

/**
 * Hello world!
 *
 */
public class App 
{
   private KnowledgeGraph _kGraph = null;

   public App(String databaseName) throws Exception {
      _kGraph = new KnowledgeGraph(databaseName);
   }
   
   public KnowledgeGraph getGraph() {
      return _kGraph;
   }

   public void loadDataset(Dataset datasetClass) {
      String datasetInfoID = datasetClass.getDatasetInfoID();
      QueryClause linearDatasetInfoQuery = new QueryClause("DatasetInfo", QueryClause.Operator.EQUALS, datasetInfoID);
      List<Node> datasetInfo = getGraph().queryNodes("DataSet_Info", linearDatasetInfoQuery);
      if(datasetInfo.size() > 1) {
         throw new RuntimeException("Dataset " + datasetInfoID + " misloaded.");
      }
      else if (0 == datasetInfo.size()) {
         // load dataset
      }
   }

}
