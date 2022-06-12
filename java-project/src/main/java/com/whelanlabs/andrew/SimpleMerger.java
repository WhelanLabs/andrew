package com.whelanlabs.andrew;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class SimpleMerger implements Merger{

   private static Logger logger = LogManager.getLogger(SimpleMerger.class);

   @Override
   public Thought merge(Thought t1, Thought t2) {
      Thought t1c = t1.clone();
      Thought t2c = t2.clone(t1c.getKey());
      
      // connect t2c to the same thought as t1c
      String t1c_thoughtNode_id = t1c.getThoughtNode().getId();
      QueryClause queryClause = new QueryClause("_from", QueryClause.Operator.EQUALS, t1c_thoughtNode_id);
      List<Edge> thoughtEdges = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
      logger.debug("thoughtEdges = " + thoughtEdges);
      for( Edge thoughtEdge : thoughtEdges ) {
         thoughtEdge.setFrom(t1c_thoughtNode_id);
      }
      App.getGardenGraph().upsert(thoughtEdges.toArray(new Edge[0]));

      // remove t2c thought and approach edge
      QueryClause approachQueryClause = new QueryClause("_to", QueryClause.Operator.EQUALS, t2c.getThoughtNode().getId());
      List<Edge> approachEdges = App.getGardenGraph().queryEdges("approach", queryClause);
      App.getGardenGraph().delete(approachEdges.get(0));
      App.getGardenGraph().delete(t2c.getThoughtNode());
      
      // create aggregating node and have both replace edges to that for the end node edges
      
      // connect the aggregating node to t1c end
      return null;
   }

}
