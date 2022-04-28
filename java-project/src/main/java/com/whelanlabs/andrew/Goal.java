package com.whelanlabs.andrew;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Goal {

   Long _time;
   
   static DateTimeFormatter standardFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
   
   public Goal(Long goalDate) {
      _time = goalDate;
   }
   
   public Goal(LocalDateTime goalDate) {
      _time = goalDate.toEpochSecond(ZoneOffset.UTC);
   }
   
   /**
    * Gets the time as GMT string. (yyyy-MM-dd'T'HH:mm:ss)
    *
    * @return the time as GMT string
    */
   public String getTimeAsGMTString() {
      LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(_time, 0, ZoneOffset.UTC);
      return localDateTime.format(standardFormatter);
   }

   public LocalDateTime getTimeAsLocalDateTime() {
      LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(_time, 0, ZoneOffset.UTC);
      return localDateTime;
   }
   
   /**
    * Gets the time.
    *
    * Time is the number of seconds since Epoch.
    * @return the time
    */
   public Long getTimeAsLong() {
      return _time;
   }

}
