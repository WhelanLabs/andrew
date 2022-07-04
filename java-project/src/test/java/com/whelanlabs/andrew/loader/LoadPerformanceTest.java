package com.whelanlabs.andrew.loader;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.whelanlabs.andrew.App;
import com.whelanlabs.andrew.dataset.StockLoader;

public class LoadPerformanceTest {

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
   public void loadStocks_goodFiles_loaded() {
      List<File> files = new ArrayList<>();
      files.add(new File("../fetchers/stock_data_fetcher/data/AA_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AAAU_2020-05-07.txt"));
      files.add(new File("../fetchers/stock_data_fetcher/data/AACG_2020-05-07.txt"));

      
      StockLoader stockLoader = new StockLoader();
      stockLoader.loadStocks(files);
      
      fail("Not yet implemented");
   }

}
