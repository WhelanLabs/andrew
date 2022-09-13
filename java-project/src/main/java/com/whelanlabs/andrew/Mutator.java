package com.whelanlabs.andrew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.QueryClause;

public class Mutator {

   private static Logger logger = LogManager.getLogger(Mutator.class);

   public static Thought createMutant(Thought thought, Integer numMutations) {

      // start with a clone
      Thought clone = thought.clone();
      
      QueryClause thoughtKeyQueryClause = new QueryClause("thought_key", QueryClause.Operator.EQUALS, clone._thoughtNode.getKey());
      QueryClause mutatableQueryClause = new QueryClause("mutation_range", QueryClause.Operator.NOT_EQUALS, null);
      List<Edge> sequenceEdges = App.getGardenGraph().queryEdges("thought_sequence", thoughtKeyQueryClause, mutatableQueryClause);

      logger.debug("sequenceEdges: " + sequenceEdges);

      for (int i = 0; i < numMutations; i++) {
         Random random = new Random();
         double rand = Math.random();
         Edge randomEdge = sequenceEdges.remove(random.nextInt(sequenceEdges.size()));
         if (null != randomEdge) {
            // clustering strength
            Integer c = 3;
            double mutationFactor = Math.pow((2 * rand) - 1, c) + 1;
            logger.debug("rand: " + rand);
            logger.debug("mutation_factor: " + mutationFactor);
            randomEdge.addAttribute("mutation_factor", mutationFactor);
            App.getGardenGraph().upsert(randomEdge);
         }
      }
      return clone;
   }

   public static List<Thought> createMutant(List<Thought> currentThoughts, int numMutations) {
      List<Thought> mutantThoughts = new ArrayList<>();
      
      for(Thought currentThought: currentThoughts) {
         Thought mutant = createMutant(currentThought, numMutations);
         mutantThoughts.add(mutant);
      }
      return mutantThoughts;
   }
}
