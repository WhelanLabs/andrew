package com.whelanlabs.andrew.process;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.whelanlabs.andrew.Thought;

public class Report {

   private static List<Float> _generationAverageScoreList = new ArrayList<>();
   private static List<Map<String, Thought>> _generationThoughtsList = new ArrayList<>();
   private static List<List<ThoughtScore>> _generationScoresList = new ArrayList<>();
   private static Long _durationSeconds = null;

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
      reportWriter.write("Andrew Training Report" + "\n");
      reportWriter.write("----------------------" + "\n");
      reportWriter.write("processing time: " + _durationSeconds + " seconds" + "\n\n");

      // see: https://stackoverflow.com/a/67370754/2418261
      SimpleRegression regression = new SimpleRegression();
      int dataListSize = _generationAverageScoreList.size();
      double dataArray[][] = new double[dataListSize][2];
      for (int i = 0; i < dataListSize; i++) {

         dataArray[i][0] = i + 1;
         dataArray[i][1] = _generationAverageScoreList.get(i);
      }

      for (int i = 0; i < dataArray.length; i++) {
         reportWriter.write("generation " + dataArray[i][0] + "\t" + "(pop. " + _generationThoughtsList.get(i).size() + ") \t" + "average score: "
               + dataArray[i][1] + "\n");
      }

      regression.addData(dataArray);
      regression.regress();

      reportWriter.write("\n" + "overall slope = " + regression.getSlope() + "\n");

      reportWriter.write("\n" + "-------------------------------------------" + "\n");
      
      for (int i = 0; i < _generationScoresList.size(); i++) {
         Integer genNum = i+1;
         reportWriter.write("\n" + "details for generation " + genNum + ": \n" + _generationScoresList.get(i) + "\n");
      }
      
      reportWriter.close();
   }

   public static void registerGeneration(Float averageGenScore, Map<String, Thought> genThoughts, List<ThoughtScore> genScores) {
      _generationAverageScoreList.add(averageGenScore);
      _generationThoughtsList.add(genThoughts);
      _generationScoresList.add(genScores);
   }

   public static void setElapsedSeconds(long duration) {
      _durationSeconds = duration;
   }

}
