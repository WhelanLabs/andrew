package com.whelanlabs.andrew.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.whelanlabs.andrew.AppTest;
import com.whelanlabs.andrew.GoalTest;
import com.whelanlabs.andrew.ThoughtTest;
import com.whelanlabs.andrew.loader.LinearDatasetTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ AppTest.class, LinearDatasetTest.class, ThoughtTest.class, GoalTest.class })

public class FunctionalSuite {
}
