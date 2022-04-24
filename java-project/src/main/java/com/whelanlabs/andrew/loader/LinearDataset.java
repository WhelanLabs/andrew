package com.whelanlabs.andrew.loader;

import java.util.List;

import com.whelanlabs.andrew.Dataset;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class LinearDataset extends Dataset {

   @Override
   public String getDatasetInfoID() {
      return "linear_test_set";
   }


}
