package com.whelanlabs.andrew.dataset;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

   private static LocalDate epoch = LocalDate.ofEpochDay(0);
   
   public Long getDateLong(LocalDate localDate) {
      Long daysSinceEpoch = ChronoUnit.DAYS.between(epoch, localDate);
      return daysSinceEpoch;
   }
}
