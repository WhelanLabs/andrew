package com.whelanlabs.andrew;

import java.util.List;

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
      QueryClause datasetInfoQuery = new QueryClause("dataset_id", QueryClause.Operator.EQUALS, datasetInfoID);
      List<Node> datasetInfo = getGraph().queryNodes("dataSet_info", datasetInfoQuery);
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
         Edge[] edgesArray = new Edge[edges.size()];
         edgesArray = edges.toArray(edgesArray);
         _kGraph.upsert(edgesArray);
         
         // lastly, create the dataSet_info node
         Node datasetInfoNode = new Node(datasetInfoID, "dataSet_info");
         datasetInfoNode.addAttribute("dataset_id", datasetInfoID);
         datasetInfoNode.addAttribute("max_time", dataset.getMaxTime());
         _kGraph.upsert(datasetInfoNode);
      }
   }

}
