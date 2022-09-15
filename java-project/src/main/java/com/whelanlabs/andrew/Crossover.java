package com.whelanlabs.andrew;

import java.util.List;

public interface Crossover {
   public Thought crossover(Thought t1, Thought t2);

   public List<Thought> createCrossovers(List<Thought> currentThoughts);
}
