package com.whelanlabs.andrew;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GoalTest {

   private static Logger logger = LogManager.getLogger(GoalTest.class);
   
   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void getTimeAsLong_validDateTime_timeIsCorrect() {
      LocalDateTime localDateTime = LocalDateTime.now();
      Goal result = new Goal(localDateTime);
      Long localDateTimeLong = localDateTime.toEpochSecond(ZoneOffset.UTC);
      Long goalTime = result.getTimeAsLong();
      assert(localDateTimeLong.equals(goalTime)): "{localDateTimeLong, goalTime} = { " + localDateTimeLong + ", " + goalTime + " }";
   }

   @Test
   public void getTimeAsGMTString_validDateTime_timeIsCorrect() {
      LocalDateTime localDateTime = LocalDateTime.now();
      Goal result = new Goal(localDateTime);
      
      // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
      String localDateTimeString = localDateTime.format(formatter);
      logger.debug("localDateTimeString = " + localDateTimeString);
      
      String goalTime = result.getTimeAsGMTString();
      assert(localDateTimeString.equals(goalTime)): "{localDateTimeString, goalTime} = { " + localDateTimeString + ", " + goalTime + " }";
   }
   
   //getTimeAsLocalDateTime
   @Test
   public void getTimeAsLocalDateTime_validDateTime_timeIsCorrect() {
      LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
      Goal result = new Goal(localDateTime);
      
      LocalDateTime goalTime = result.getTimeAsLocalDateTime();
      
      assert(localDateTime.equals(goalTime)): "{localDateTime, goalTime} = { " + localDateTime + ", " + goalTime + " }";
   }
}

