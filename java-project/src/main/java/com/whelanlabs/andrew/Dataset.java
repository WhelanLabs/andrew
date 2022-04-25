package com.whelanlabs.andrew;

import java.util.List;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;

public abstract class Dataset {

   public abstract String getDatasetInfoID();

   protected abstract List<Node> getNodesToLoad();

   protected abstract List<Edge> getEdgesToLoad();

   protected abstract Object getMaxTime();
}
