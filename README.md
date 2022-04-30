# Andrew

## Overview

Andrew is a code-block-based adaptive genetic algorithm time-series forecasting graph processing AI.

* code-block-based: the constructs that are the building block for each genetic solution are blocks of code that have have an input shape and an output shape.  The blocks are similar to blocks in the programming language Scratch.  The limitation of possible connections of blocks allow for resulting solutions to be well-formed.  The parameters to blocks have defined valid values; this allows genetic operationsto occur on parameters while maintaining valid values.

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
In addition to supporting genetic operations in the genetic solutions, the fitness functions also support genetic operations within their defined ranges.  This allows fitness functions to compete.  As an example, imagine a base fitness function that looks to identify stocks that will do the best in 1-year period; it may be that a fitness function that scores on a quarterly basis will be able to idetify higher annualized rates of return.  As long as the underlying scoring criteria is correct (in this case: rate of return), changing the parameters of the fitness functions might produce improved overall results. <P/>
[Note: It is of interest that a program can have an ability to somewhat change its goals.]

## Architecture

### Graphs

#### Knowladge Graph

#### Garden Graph

## Building the Project

### main build command
	mvn clean install


### Start ArangoDB in Docker:
	docker run -p 8529:8529 -e ARANGO_ROOT_PASSWORD=openSesame arangodb/arangodb:3.8.2


### In order to build the uber jar run:
	mvn clean install assembly:single


### To display available updates to Maven depenedencies:
 (see also: https://www.baeldung.com/maven-dependency-latest-version)
 
	mvn versions:display-dependency-updates
	
	
## To build licensing information:
The results are written to [ ./java-project/target/site/dependencies.html ].

	mvn site


## To build Javadoc
	mvn javadoc:javadoc