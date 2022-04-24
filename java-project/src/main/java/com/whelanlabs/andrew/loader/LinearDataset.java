package com.whelanlabs.andrew.loader;

import java.util.ArrayList;
import java.util.List;

import com.whelanlabs.andrew.Dataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class LinearDataset extends Dataset {

   @Override
   public String getDatasetInfoID() {
      return "linear_test_set";
   }

   @Override
   protected List<Node> getNodesToLoad() {
      List<Node> results = new ArrayList<>();
      
      // TODO: populate the results
      
      return results;
   }

   @Override
   protected List<Edge> getEdgesToLoad() {
      List<Edge> results = new ArrayList<>();
      
      // TODO: populate the results
      
      return results;
   }


}
