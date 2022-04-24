package com.whelanlabs.andrew;

import java.util.List;

import com.whelanlabs.andrew.loader.LinearDataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

/**
 * Hello world!
 *
 */
public class App {
   private KnowledgeGraph _kGraph = null;

   public App(String databaseName) throws Exception {
      _kGraph = new KnowledgeGraph(databaseName);
   }

   public KnowledgeGraph getGraph() {
      return _kGraph;
   }

   public void loadDataset(Dataset dataset) {
      String datasetInfoID = dataset.getDatasetInfoID();
      QueryClause linearDatasetInfoQuery = new QueryClause("dataset_id", QueryClause.Operator.EQUALS, datasetInfoID);
      List<Node> datasetInfo = getGraph().queryNodes("dataSet_info", linearDatasetInfoQuery);
      if (datasetInfo.size() > 1) {
         throw new RuntimeException("Dataset " + datasetInfoID + " misloaded.");
      } else if (0 == datasetInfo.size()) {
         // load dataset nodes
         List<Node> nodes = dataset.getNodesToLoad();
         Node[] nodesArray = new Node[nodes.size()];
         nodesArray = nodes.toArray(nodesArray);
         _kGraph.upsert(nodesArray);

         // load dataset edges
         List<Edge> edges = dataset.getEdgesToLoad();
         Node[] edgesArray = new Node[edges.size()];
         edgesArray = nodes.toArray(edgesArray);
         _kGraph.upsert(nodesArray);
         
         // lastly, create the dataSet_info node
         
      }
   }

}
