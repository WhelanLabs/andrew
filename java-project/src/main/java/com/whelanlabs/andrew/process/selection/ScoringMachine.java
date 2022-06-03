package com.whelanlabs.andrew.process.selection;

import java.util.List;

import com.whelanlabs.andrew.process.evaluate.Evaluation;

public interface ScoringMachine {

   public List<ThoughtScore> scoreAndRank(List<Evaluation> evualationResults);

}
