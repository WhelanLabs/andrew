package com.whelanlabs.andrew;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Goal {

   LocalDateTime _time;
   
   public Goal(LocalDateTime goalDate) {
      _time = goalDate;
   }
   
   public String getTimeAsGMTString() {
      //SimpleDateFormat sdf;
      //sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
      //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      //String result = sdf.format(_time);
      //return result;
      return _time.toString();
   }

}
