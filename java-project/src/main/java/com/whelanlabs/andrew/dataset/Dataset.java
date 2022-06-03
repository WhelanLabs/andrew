package com.whelanlabs.andrew.dataset;

import java.util.List;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public abstract class Dataset {

   public abstract String getDatasetInfoID();

   public abstract List<Node> getNodesToLoad();

   public abstract List<Edge> getEdgesToLoad();

   public abstract Object getMaxTime();
}
