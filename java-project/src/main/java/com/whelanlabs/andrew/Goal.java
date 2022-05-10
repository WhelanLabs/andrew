package com.whelanlabs.andrew;

import java.time.format.DateTimeFormatter;

import com.arangodb.model.TraversalOptions.Direction;

public class Goal {

   private String _targetProperty;

   private String _relationType;

   private Direction _direction;

   private Integer _distance;

   static DateTimeFormatter standardFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

   public Goal(String relationType, Direction direction, Integer distance, String targetProperty) {
      
      if(!(direction == Direction.inbound || direction == Direction.outbound) ) {
         throw new RuntimeException("invalid direction (" + direction + ")");
      }
      
      if(distance <1 ) {
         throw new RuntimeException("invalid distance (" + distance + ")");
      }
      
      _relationType =  relationType; 
      _direction = direction; 
      _distance = distance;
      _targetProperty = targetProperty;
   }

   public String getTargetProperty() {
      return _targetProperty;
   }
   
   public String getRelationType() {
      return _relationType;
   }
   
   public Direction getDirection() {
      return _direction;
   }
   
   public Integer getDistance() {
      return _distance;
   }

}
