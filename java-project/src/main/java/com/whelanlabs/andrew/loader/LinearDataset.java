package com.whelanlabs.andrew.loader;

import java.util.ArrayList;
import java.util.List;

import com.whelanlabs.andrew.Dataset;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class LinearDataset extends Dataset {

   private final Integer size = 1000;

   @Override
   public String getDatasetInfoID() {
      return "linear_test_set";
   }

   @Override
   protected List<Node> getNodesToLoad() {
      List<Node> results = new ArrayList<>();
      for(int i=1; i<=size ; i++) {
         Node result = new Node("LinearDatasetNode_"+i, "LinearDatasetNode");
         result.addAttribute("time", i);
         results.add(result);
      }
      return results;
   }

   @Override
   protected List<Edge> getEdgesToLoad() {
      List<Edge> results = new ArrayList<>();
      for(int i=1; i<size ; i++) {
         Edge result = new Edge("LinearDatasetEdge__"+i + "_to_" + (i+1), "LinearDatasetNode_"+i, "LinearDatasetNode_"+(i+1), "LinearDatasetNode", "LinearDatasetNode", "LinearDatasetEdge");
         result.addAttribute("time", i);
         results.add(result);
      }
      return results;
   }


}
