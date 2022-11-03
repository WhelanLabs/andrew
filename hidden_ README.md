# Andrew

## Introduction

Andrew is an time-series forecasting AI application that uses a genetic algorithm on graphs of code-block-based genes. These graphs are referred to as Thoughts.  Thought Graphs are are associated with with a specific target Goal.  As Andrew runs, the Thoughts are combined and mutated into new thoughts.

Initial Thoughts, referred to as Seed Thoughts, can represent any formula for solving a problem, but in general they should model basic concepts, such as linear movement, and correlation.

Thoughts are mainly composed of Thought Operations (nodes) and Thought Sequences (edges).  The Thought Sequences edges pass data between Thought Operations.  The Thought Sequences also contain properties that support mutation, and identify the range of mutations supported.  Mutations may be suppoeted for virtually any data type, from simple mutations in Integer values to more complex mutations within sets of valid value sets; for example, changing the name of a target object or relation name within the World Graph. 



-----

Andrew currently only supports non-cyclic Thoughts.

Andrew operates using two separate graph repositories.  The internal thought-related operations (Goals and Thoughts) operate in what is called the Garden Graph which changes as Andrew runs, while the data on which forecasts are based is contained in a static graph that is called the World Graph.

Besides producing outputs for consumption for downstream Thought Operations, Thought Operations may also produce side effects.  For example, having a Thought Operation that sends out e-mail should be considered a valid operation.

Andrew is designed to support horizontal scaling for both its graph data sources and its processing power.



## Motivations

### Under-Researched Topics

### Interactive Capable

## Logical Constructs

### Genetic Solutions
Within Andrew, Genetic Solutions are referred to as Thoughts.

### Fitness Functions
In addition to supporting genetic operations in the genetic solutions, the fitness functions also support genetic operations within their defined ranges.  This allows fitness functions to compete.  As an example, imagine a base fitness function that looks to identify stocks that will do the best in 1-year period; it may be that a fitness function that scores quarterly will be able to identify higher annualized rates of return.  As long as the underlying scoring criterion is correct (in this case: rate of return), changing the parameters of the fitness functions might produce improved overall results. <P/>
[Note: It is of interest that a program can have the ability to somewhat change its goals.]

## Architecture

### Graphs

#### Knowledge Graph

#### Garden Graph

