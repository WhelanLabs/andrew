package com.whelanlabs.andrew.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.andrew.App;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Element;
import com.whelanlabs.kgraph.engine.ElementHelper;
import com.whelanlabs.kgraph.engine.Node;

public class StockLoader {

   private static Logger logger = LogManager.getLogger(StockLoader.class);

   private LocalDate epoch = LocalDate.ofEpochDay(0);

   public StockLoader() {
      //
   }

   public void loadStocks(List<File> files) {

      List<Element> elements = new ArrayList<>();

      try {
         for (File file : files) {
            String symbol = null;
            logger.debug("current file.path: " + file.getAbsolutePath());
            Scanner scanner = new Scanner(file);
            Boolean firstLine = true;
            while (scanner.hasNextLine()) {
               String currentLine = scanner.nextLine();
               if (firstLine) {
                  symbol = (file.getName().split("_"))[0];

                  // create stockSymbol Node
                  Node symbolNode = new Node(symbol, "stockSymbol");
                  // symbolNode.addAttribute("name", "n1" );
                  elements.add(symbolNode);

                  firstLine = false;
               } else {
                  String[] fields = currentLine.split(",");
                  String[] dateFields = fields[0].split("-");
                  LocalDate eventDate = LocalDate.of(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]), Integer.valueOf(dateFields[2]));
                  long daysSinceEpoch = ChronoUnit.DAYS.between(epoch, eventDate);

                  Float dayHigh = Float.valueOf(fields[1]);
                  Float dayLow = Float.valueOf(fields[2]);
                  Float dayClose = Float.valueOf(fields[3]);
                  Float dayAdjClose = Float.valueOf(fields[4]);
                  Integer dayVolume = Integer.valueOf(fields[5]);

                  // TODO: create date Node - also keep track of the date has already been added

                  // TODO: create stockSymbolOnDate Edge

                  // TODO: this block is jts sample code. delete it before done.
                  String thoughtKey = ElementHelper.generateKey();
                  final Node n1 = new Node(thoughtKey, "thought");
                  n1.addAttribute("name", "n1");
                  final Node goalNode = new Node(ElementHelper.generateKey(), "goal");
                  Edge e0 = new Edge(ElementHelper.generateKey(), goalNode, n1, "approach");
                  e0.addAttribute("thought_key", n1.getKey());
                  e0.addAttribute("name", "e0");
                  

//                  System.out.print("symbol = " + symbol + " ### ");
//                  System.out.print("daysSinceEpoch = " + daysSinceEpoch + " ### ");
//                  System.out.println(currentLine);

               }
            }
            
            // upsert for each input file
            App.getDataGraph().upsert(elements);
            
            scanner.close();
         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
}
