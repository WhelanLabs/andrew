package com.whelanlabs.andrew.poc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.Thought;
import com.whelanlabs.andrew.ThoughtTest;
import com.whelanlabs.andrew.dataset.CSVLoader;
import com.whelanlabs.andrew.dataset.LinearDataset;



public class ExampleOne {

   private static Logger logger = LogManager.getLogger(ThoughtTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      String databaseName = "ExampleOne_database";
      App.initialize(databaseName);
      App.getGardenGraph().flush();
      App.getDataGraph().flush();
      App.loadDatasetToDataGraph(new LinearDataset());
   }

   /**
    * The main method.
    * <p/>
    * see also: https://www.geeksforgeeks.org/encoding-methods-in-genetic-algorithm/
    *
    * @param args the arguments
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public static void main(String[] args) throws IOException {
      // load the test data
      List<File> files = new ArrayList<>();
      List<String> tickers = new ArrayList<>();
      String baseDir = "../fetchers/stock_data_fetcher/data/";
      File f = new File(baseDir);
      String[] baseFileNames = f.list();
      for (String baseFileName : baseFileNames) {
         if(baseFileName.startsWith("A")) {
            String filePath = baseDir + baseFileName;
            String ticker = baseFileName.substring(0, baseFileName.indexOf("_")-1);
            tickers.add(ticker);
            files.add(new File(filePath));
            logger.debug("adding ticker " + ticker + "(" + baseFileName + ")");
         }

      }
      CSVLoader stockLoader = new CSVLoader();
      stockLoader.loadStocks(files);
      
      // load the seed thought
      String filePath = "./src/main/resources/initial_thoughts/linear_growth/linear_growth_thought.json";
      String content = new String(Files.readAllBytes(Paths.get(filePath)));
      Thought rootThought = App.loadThoughtFromJson("linear_growth_thought", content);

      // repeat

      // generate crossover (name children based on parents - ex: [PID]_[PPID])

      // generate mutants (copy and mutate) (name mutants based on base - ex: [PID]-[MutationID]_[PPID])

      // loop through a set of test cases

      // sum the score for each thought

      // cull the herd of thought/goal when limited for resources.
      // Have culling be statistical some sometimes bad thoughts survive.


      // until things don't get better (end of repeat-until)

      // write the results

      // compare the thoughts for times in the future

   }

}
