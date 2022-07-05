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

public class StockDataLoaderTest {

   private static Logger logger = LogManager.getLogger(StockDataLoaderTest.class);

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
      files.add(new File("../fetchers/stock_data_fetcher/data/AACG_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AACG_2020-05-07.txt"));
      
      StockLoader stockLoader = new StockLoader();

      stockLoader.loadStocks(files);
      
      Long count = App.getDataGraph().getCount("stockOnDate");
      
      logger.debug("count = " + count);
      
      assert (count > 290);
      assert (count < 500);
   }

   
   @Test(expected = FileNotFoundException.class)
   public void loadStocks_badFile_exception() throws FileNotFoundException {
      List<File> files = new ArrayList<>();
      files.add(new File("../fetchers/stock_data_fetcher/data/BAD_FILE.txt"));
      
      StockLoader stockLoader = new StockLoader();

      stockLoader.loadStocks(files);
   }
}
