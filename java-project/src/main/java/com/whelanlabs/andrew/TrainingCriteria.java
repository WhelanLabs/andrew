package com.whelanlabs.andrew;

import java.time.LocalDate;

import com.whelanlabs.andrew.dataset.DateUtils;

public class TrainingCriteria {

   private static DateUtils dateUtils = new DateUtils();
   
   private Integer _numGenerations;
   private Integer _questsPerGeneration;
   private Long _startDateLong;
   private Long _endDateLong;
   
   public TrainingCriteria(Integer numGenerations, Integer questsPerGeneration, LocalDate startDate, LocalDate endDate) {
      _numGenerations = numGenerations;
      _questsPerGeneration = questsPerGeneration;
      _startDateLong = dateUtils.getDateLong(startDate);
      _endDateLong = dateUtils.getDateLong(endDate);
   }
   
   public Integer getNumGenerations() {
      return _numGenerations;
   }
   
   public Integer getQuestsPerGeneration() {
      return _questsPerGeneration;
   }
   
   public Long getStartDateLong() {
      return _startDateLong;
   }
   
   public Long getEndDateLong() {
      return _endDateLong;
   }
}
