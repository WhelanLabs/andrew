package com.whelanlabs.andrew.process;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Report {

   private static Map<Integer, Float> averageGenScoreMap = new HashMap<>();
   private Report() {
      // do nothing. Static global class.
   }

   public static void print() throws Exception {
      // TODO Auto-generated method stub
      String path = "./target/report/";
      Files.createDirectories(Paths.get(path));
      File dir = new File(path);
      File file = new File(dir, "Andrew_training_report_" + System.currentTimeMillis() + ".txt");
      FileWriter reportWriter = new FileWriter(file);
      reportWriter.write("Andrew Training Report\n");
      reportWriter.write("----------------------\n");
      
      // see: https://stackoverflow.com/a/67370754/2418261
      List<Float> dataList = new ArrayList<>();
      SimpleRegression regression = new SimpleRegression();
      int dataListSize = averageGenScoreMap.size();
      double dataArray[][] = new double[dataListSize][2];
      for (int i = 0; i< dataListSize; i++) {
         
         dataArray[i][0] = i+1;
         dataArray[i][1] = averageGenScoreMap.get(i);
      }
      
      for(int i = 0; i < dataArray.length; i++) {
         reportWriter.write("generation " + dataArray[i][0] + "\t" + "average score: " + dataArray[i][1] + "\n");
      }
      
      regression.addData(dataArray);
      regression.regress();
      
      reportWriter.write("\n" + "overall slope = " + regression.getSlope());
      
      reportWriter.close();
   }

   private static char[] getScoresByGenTable() {
      // TODO Auto-generated method stub
      return null;
   }

   public static void registerAverageGenScore(Integer currentGen, Float averageGenScore) {
      averageGenScoreMap.put(currentGen, averageGenScore);
   }

//   public static void registerGenScores(Integer currentGen, List<ThoughtScore> scores) {
//      // TODO Auto-generated method stub
//   }
   

}
