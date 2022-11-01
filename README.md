# Andrew

## Overview

Andrew is a code-block-based adaptive genetic algorithm time-series forecasting graph processing AI.

* code-block-based: the constructs that are the building block for each genetic solution are blocks of code that have an input shape and an output shape.  The blocks are similar to blocks in the programming language Scratch.  The limitation of possible connections of blocks allows for resulting solutions to be well-formed.  The parameters to blocks have defined valid values; this allows genetic operations to occur on parameters while maintaining valid values.

* adaptive genetic algorithm:

* time-series:

* forecasting:

* graph processing:

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

## Dependencies

### KGraph & ArangoDB

Andrew depends on the KGraph library.  In turn, KGraph depends on ArangoDB.  There are
two main ways to run ArangoDB in support of Andrew, are:

### Start ArangoDB in Docker

	docker run -p 8529:8529 -e ARANGO_ROOT_PASSWORD=openSesame arangodb/arangodb:latest


Using Docker provides a simple approach for small cases, but tends to run slow for anything
larger than unit tests.  The issues likely causing the slow performance of ArangoDB on
Docker can be seen in the container log file as:


	WARNING [118b0] {memory} maximum number of memory mappings per process is 65530, which seems too low. it is recommended to set it to at least 512000


### Start ArangoDB as a Process

This approach runs the code at least an order of magnitude faster.


## Building the Project

### main build command

	mvn clean install
	
	mvn clean source:jar javadoc:jar install -DskipTests

### To build the uber jar, run:

	mvn clean install assembly:single

### To display available updates to Maven dependencies:
 (see also: https://www.baeldung.com/maven-dependency-latest-version)
 
	mvn versions:display-dependency-updates
	
## To build licensing information:

	mvn site


The results are written to [ ./java-project/target/site/dependencies.html ].



## To build Javadoc
	mvn javadoc:javadoc
	
## to generate sample graph diagram images (requires Graphviz)
	dot -Tsvg .\target\dot_files\exportDot_goodThought_goodDot.dot > .\target\dot_files\exportDot_goodThought_goodDot.svg

