package com.whelanlabs.andrew.process;

import java.util.List;

import com.whelanlabs.andrew.process.Evaluation;

public interface ScoringMachine {

   public List<ThoughtScore> scoreAndRank(List<Evaluation> evualationResults);

}
