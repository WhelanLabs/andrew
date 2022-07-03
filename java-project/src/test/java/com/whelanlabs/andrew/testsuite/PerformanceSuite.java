package com.whelanlabs.andrew.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.whelanlabs.andrew.loader.LoadPerformanceTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({LoadPerformanceTest.class})

public class PerformanceSuite {
}