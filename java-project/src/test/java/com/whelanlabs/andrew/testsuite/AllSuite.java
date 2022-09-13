package com.whelanlabs.andrew.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.whelanlabs.andrew.AppTest;
import com.whelanlabs.andrew.AveragePercentageScoringMachineTest;
import com.whelanlabs.andrew.EvaluatorTest;
import com.whelanlabs.andrew.GoalTest;
import com.whelanlabs.andrew.OperationsTest;
import com.whelanlabs.andrew.ThoughtTest;
import com.whelanlabs.andrew.loader.CSVLoaderTest;
import com.whelanlabs.andrew.loader.LinearDatasetTest;
import com.whelanlabs.andrew.loader.LoadPerformanceTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ AppTest.class, LinearDatasetTest.class, ThoughtTest.class, EvaluatorTest.class, OperationsTest.class,
   AveragePercentageScoringMachineTest.class, CSVLoaderTest.class, LoadPerformanceTest.class, GoalTest.class })

public class AllSuite {
}