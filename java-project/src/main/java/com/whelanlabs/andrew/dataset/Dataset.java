package com.whelanlabs.andrew.dataset;

import java.util.List;

import com.whelanlabs.pgraph.engine.Edge;
import com.whelanlabs.pgraph.engine.Node;

/**
 * The Interface Dataset.
 */
public interface Dataset {

   /**
    * Gets the dataset info ID.
    *
    * @return the dataset info ID
    */
   public abstract String getDatasetInfoID();

   /**
    * Gets the nodes to load.
    *
    * @return the nodes to load
    */
   public abstract List<Node> getNodesToLoad();

   /**
    * Gets the edges to load.
    *
    * @return the edges to load
    */
   public abstract List<Edge> getEdgesToLoad();

   /**
    * Gets the max time.
    *
    * @return the max time
    */
   public abstract Object getMaxTime();
}
