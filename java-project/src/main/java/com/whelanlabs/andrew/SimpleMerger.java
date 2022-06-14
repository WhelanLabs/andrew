package com.whelanlabs.andrew;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arangodb.model.TraversalOptions.Direction;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class SimpleMerger implements Merger{

   private static Logger logger = LogManager.getLogger(SimpleMerger.class);

   @Override
   public Thought merge(Thought t1, Thought t2) {
      Thought t1c = t1.clone();
      Thought t2c = t2.clone();
      
      // connect sequences from t2c to t1c
      String t2c_thoughtNode_id = t2c.getThoughtNode().getId();
      QueryClause queryClause = new QueryClause("_from", QueryClause.Operator.EQUALS, t2c_thoughtNode_id);
      List<Edge> thoughtEdges = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
      logger.debug("thoughtEdges = " + thoughtEdges);
      for( Edge thoughtEdge : thoughtEdges ) {
         thoughtEdge.setFrom(t1c.getThoughtNode().getId());
      }
      App.getGardenGraph().upsert(thoughtEdges.toArray(new Edge[0]));

      // remove t2c thought and approach edge
      QueryClause approachQueryClause = new QueryClause("_to", QueryClause.Operator.EQUALS, t2c.getThoughtNode().getId());
      List<Edge> approachEdges = App.getGardenGraph().queryEdges("approach", approachQueryClause);
      //List<Edge> allApproachEdges = App.getGardenGraph().queryEdges("approach");
      App.getGardenGraph().delete(approachEdges.get(0));
      App.getGardenGraph().delete(t2c.getThoughtNode());
      
      // create aggregating node
      final Node aggNode = new Node(ElementHelper.generateKey(), "thought_operation");
      aggNode.addAttribute("thought_key", t1c.getThoughtNode().getKey());
      aggNode.addAttribute("operationName", "average");
      
      // have both end edges updated to connect to aggNode (including changing the output names)
      
      // connect the aggregating node to t1c end node
      
      // delete t2c end node
      
      // connect the aggregating node to t1c end
      
      // set the thought_key for t2c elements ("thought_operation", "thought_sequence") to t1c key
      
      return t1c;
   }

}
