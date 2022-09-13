package com.whelanlabs.andrew;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

public class SimpleCrossover implements Crossover{

   private static Logger logger = LogManager.getLogger(SimpleCrossover.class);

   @Override
   public Thought crossover(Thought t1, Thought t2) {
      logger.debug("t1.id = " + t1.getThoughtNode().getId());
      logger.debug("t2.id = " + t2.getThoughtNode().getId());

      Thought t1c = t1.clone();
      Thought t2c = t2.clone();
      
      logger.debug("t1c.id = " + t1c.getThoughtNode().getId());
      logger.debug("t2c.id = " + t2c.getThoughtNode().getId());
      
      // connect sequences from t2c to t1c
      String t2c_thoughtNode_id = t2c.getThoughtNode().getId();
      logger.debug("t2c_thoughtNode_id = " + t2c_thoughtNode_id);
      QueryClause queryClause = new QueryClause("_from", QueryClause.Operator.EQUALS, t2c_thoughtNode_id);
      List<Edge> thoughtEdges = App.getGardenGraph().queryEdges("thought_sequence", queryClause);
      List<Edge> allThoughtEdges = App.getGardenGraph().queryEdges("thought_sequence");
      logger.debug("allThoughtEdges = " + allThoughtEdges);
      logger.debug("thoughtEdges = " + thoughtEdges);
      for( Edge thoughtEdge : thoughtEdges ) {
         thoughtEdge.setFrom(t1c.getThoughtNode().getId());
      }
      App.getGardenGraph().upsert(thoughtEdges.toArray(new Edge[0]));

      // remove t2c thought and approach edge
      QueryClause approachQueryClause = new QueryClause("_to", QueryClause.Operator.EQUALS, t2c.getThoughtNode().getId());
      List<Edge> approachEdges = App.getGardenGraph().queryEdges("approach", approachQueryClause);
      App.getGardenGraph().delete(approachEdges.get(0));
      App.getGardenGraph().delete(t2c.getThoughtNode());
      
      // create aggregating node
      final Node aggNode = new Node(ElementHelper.generateKey(), "thought_operation");
      aggNode.addAttribute("thought_key", t1c.getThoughtNode().getKey());
      aggNode.addAttribute("operationName", "average");
      App.getGardenGraph().upsert(aggNode);

      
      // have both end edges updated to connect to aggNode (including changing the output names)
      // "thought_result", "thought_sequence"
      QueryClause thoughtKey1QueryClause = new QueryClause("thought_key", QueryClause.Operator.EQUALS, t1c.getThoughtNode().getKey());
      List<Node> t1cResultNodes = App.getGardenGraph().queryNodes("thought_result", thoughtKey1QueryClause);
      String t1cResultId = t1cResultNodes.get(0).getId();
      QueryClause t1cResultSequenceClause = new QueryClause("_to", QueryClause.Operator.EQUALS, t1cResultId);
      List<Edge> t1cResultEdges = App.getGardenGraph().queryEdges("thought_sequence", t1cResultSequenceClause);
      t1cResultEdges.get(0).addAttribute("output", "input1");
      t1cResultEdges.get(0).setTo(aggNode.getId());
      t1cResultEdges.get(0).addAttribute(Edge.rightTypeAttrName, "thought_operation");
      App.getGardenGraph().upsert(t1cResultEdges.get(0));
      // TODO: these two blocks scream for a helper method
      QueryClause thoughtKey2QueryClause = new QueryClause("thought_key", QueryClause.Operator.EQUALS, t2c.getThoughtNode().getKey());
      List<Node> t2cResultNodes = App.getGardenGraph().queryNodes("thought_result", thoughtKey2QueryClause);
      String t2cResultId = t2cResultNodes.get(0).getId();
      QueryClause t2cResultSequenceClause = new QueryClause("_to", QueryClause.Operator.EQUALS, t2cResultId);
      List<Edge> t2cResultEdges = App.getGardenGraph().queryEdges("thought_sequence", t2cResultSequenceClause);
      t2cResultEdges.get(0).addAttribute("output", "input2");
      t2cResultEdges.get(0).setTo(aggNode.getId());
      t2cResultEdges.get(0).addAttribute(Edge.rightTypeAttrName, "thought_operation");
      t2cResultEdges.get(0).addAttribute("thought_key", t1c.getThoughtNode().getKey());
      App.getGardenGraph().upsert(t2cResultEdges.get(0));


      // connect the aggregating node to t1c end node
      Edge e9 = new Edge(ElementHelper.generateKey(), aggNode, t1cResultNodes.get(0), "thought_sequence");
      e9.addAttribute("thought_key", t1c.getThoughtNode().getKey());
      e9.addAttribute("name", "e9" );
      e9.addAttribute("mutation_range", "FLOAT:>0" );
      e9.addAttribute("input", "RESULT" );
      e9.addAttribute("output", "output");
      App.getGardenGraph().upsert(e9);

      
      // delete t2c end node
      App.getGardenGraph().delete(t2cResultNodes.get(0));
      
      // set the thought_key for t2c elements ("thought_operation", "thought_sequence") to t1c key
      List<Node> t2cOperationNodes = App.getGardenGraph().queryNodes("thought_operation", thoughtKey2QueryClause);
      for(Node t2cOperationNode : t2cOperationNodes) {
         t2cOperationNode.addAttribute("thought_key", t1c.getThoughtNode().getKey());
         App.getGardenGraph().upsert(t2cOperationNode);
      }
      List<Edge> t2cSequenceEdges = App.getGardenGraph().queryEdges("thought_sequence", thoughtKey2QueryClause);
      for(Edge t2cSequenceEdge : t2cSequenceEdges) {
         t2cSequenceEdge.addAttribute("thought_key", t1c.getThoughtNode().getKey());
         App.getGardenGraph().upsert(t2cSequenceEdge);
      }
      
      return t1c;
   }

   @Override
   public List<Thought> createCrossovers(List<Thought> currentThoughts) {
      Random rand = new Random();
      Integer numCrossovers = currentThoughts.size();
      
      for(int i = 0; i<numCrossovers; i++) {
         Integer secondParentNum = rand.nextInt(currentThoughts.size() );
         // bookmark here;
      }
      return null;
   }

}
