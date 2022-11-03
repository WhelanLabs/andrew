# Andrew

## Introduction

Andrew is an time-series forecasting AI application that uses a genetic algorithm on graphs of code-block-based genes. These graphs are referred to as Thoughts.  Thought Graphs are are associated with with a specific target Goal.  As Andrew runs, the Thoughts are combined and mutated into new thoughts.  Andrew starts with a given set of initial Thoughts, referred to as Seed Thoughts, which can represent any formula for solving a problem, but in general they should model basic concepts, such as linear movement, and correlation.

Thoughts are mainly composed of Thought Operations (nodes) and Thought Sequences (edges).  The Thought Sequences are edges theat pass data between Thought Operations. The Thought Operations run simple processes based on their defined operation type.  Besides passing data between Thought Operations, Thought Sequences also contain properties that support mutation on the passed data.  These mutation related properties identify the range of mutations supported, and specify the amount of current mutation.  Mutations may be supported for virtually any data type, from simple mutations in Integer values to more complex mutations within sets of valid value sets; for example, changing the name of a target object or relation name within the World Graph. 

## Motivations

### Under-Researched Topics

### Interactive Capable

-----

## unstructured thoughts

Andrew currently only supports non-cyclic Thoughts.

Andrew operates using two separate graph repositories.  The internal thought-related operations (Goals and Thoughts) operate in what is called the Garden Graph which changes as Andrew runs, while the data on which forecasts are based is contained in a static graph that is called the World Graph.

Besides producing outputs for consumption for downstream Thought Operations, Thought Operations may also produce side effects.  For example, having a Thought Operation that sends out e-mail should be considered a valid operation.

Andrew is designed to support horizontal scaling for both its graph data sources and its processing power.

The amount of mutation for a Thought, both the number of mutations and the magnitude of each mutation, may be dynamically adjusted for a thought based on its general fitness.  

Future: fitness functions may also subject to evolution via genetic algorithms within defined parameters.  As an example, Fitness Functions that measure stock picking Thoughts on a quarterly basis for a year may proove to be more fit at finding profit than a once-a-year Firness Function even after accounting for transaction fees.
[Note: Given that side effects are allowed, a research topic of the difference between predicting and causing results should be considered.]


## Current Prototype

missing some concepts mentioned abov

### current crossover logic

### current mutation logic

### Fitness Functions
In addition to supporting genetic operations in the genetic solutions, the fitness functions also support genetic operations within their defined ranges.  This allows fitness functions to compete.  As an example, imagine a base fitness function that looks to identify stocks that will do the best in 1-year period; it may be that a fitness function that scores quarterly will be able to identify higher annualized rates of return.  As long as the underlying scoring criterion is correct (in this case: rate of return), changing the parameters of the fitness functions might produce improved overall results. <P/>
[Note: It is of interest that a program can have the ability to somewhat change its goals.]

### Graphs

#### Knowledge Graph

#### Garden Graph

### Results Report
processing generates a results report

## Next Steps

### GUI Thought Editor

### additional seed thoughts

### additional crossover options

### horizontally scaled test environment



