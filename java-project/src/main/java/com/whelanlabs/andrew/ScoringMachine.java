package com.whelanlabs.andrew;

import java.util.List;

public interface ScoringMachine {

   public List<ThoughtScore> scoreAndRank(List<Evaluation> evualationResults);

}
