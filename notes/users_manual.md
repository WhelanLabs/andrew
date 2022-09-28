# Overview

This document provides information to users wishing to leverage andrew for their own goals.

# Fundamentals



## Goals

Goals are desires for the user.  The most common type of these with Andrew is to have Andrew forecast future state.  In order to have Andrew Goals predict future state, specific charisteristics (properties) of each goal must be specified. They are:

* __targetDistance__: The distance into the future for which the target result is desired.  The target distance is a Long value where each unit corresponds to a day.

* __targetType__: The node type containing the value being forecasted.

* __targetProperty__: The property name containing the value being forecasted.

* __targetRelation__: This is the relationship between the identified date object and the targetType.

* __targetRelationDirection__: This is the direction to be traversed along the targetRelation from the date object to the targetType. 


## 
