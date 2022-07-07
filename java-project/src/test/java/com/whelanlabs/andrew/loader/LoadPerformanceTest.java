package com.whelanlabs.andrew.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.dataset.StockLoader;

public class LoadPerformanceTest {

   private static Logger logger = LogManager.getLogger(LoadPerformanceTest.class);

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      App.initialize("load_performance_tests");
      App.getDataGraph().flush();
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
      App.getDataGraph().cleanup();
   }

   @Test
   public void loadStocks_goodFiles_loaded() throws FileNotFoundException {
      List<File> files = new ArrayList<>();
      files.add(new File("../fetchers/stock_data_fetcher/data/AA_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AAAU_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AACG_2020-05-07.txt"));

      
      StockLoader stockLoader = new StockLoader();
      
      long startTime = System.currentTimeMillis();
      stockLoader.loadStocks(files);
      long endTime = System.currentTimeMillis();
      long elapsed = (endTime - startTime)/1000;
      logger.debug("loadStocks_goodFiles_loaded took " + elapsed + " seconds");
      
      if(elapsed > 10) {  // 10 minutes
         logger.warn("loadStocks_goodFiles_loaded took too long!");
      }
      
      Long count = App.getDataGraph().getCount("stockOnDate");
      
      assert (count > 10000);
      logger.debug("count = " + count);

   }

}
