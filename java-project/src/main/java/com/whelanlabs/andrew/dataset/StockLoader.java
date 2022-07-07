package com.whelanlabs.andrew.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.whelanlabs.andrew.App;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Element;
import com.whelanlabs.kgraph.engine.Node;

public class StockLoader {

   private static Logger logger = LogManager.getLogger(StockLoader.class);

   private LocalDate epoch = LocalDate.ofEpochDay(0);

   public StockLoader() {
      //
   }

   public void loadStocks(List<File> files) throws FileNotFoundException {

      List<Element> elements = new ArrayList<>();
      Set<Long> dates = new HashSet<>();

      Integer fileNumber = 0;
      Integer numFiles = files.size();
      for (File file : files) {
         fileNumber++;
         String symbol = null;
         
         Long count = App.getDataGraph().getCount("stockOnDate");
         logger.debug("file #" + fileNumber + " of " + numFiles + " file.path: " + file.getAbsolutePath() + "(stockOnDate.count = " + count + ")");
         
         Scanner scanner = new Scanner(file);
         Boolean firstLine = true;
         Node stockNode = null;
         String currentLine = null;
         Integer lineNum = 0;

         while (scanner.hasNextLine()) {
            lineNum++;
            try {
               currentLine = scanner.nextLine();
               if (firstLine) {
                  symbol = (file.getName().split("_"))[0];

                  // create stockSymbol Node
                  stockNode = new Node(symbol, "stockSymbol");
                  elements.add(stockNode);

                  firstLine = false;
               } else {
                  String[] fields = currentLine.split(",");
                  String[] dateFields = fields[0].split("-");
                  LocalDate eventDate = LocalDate.of(Integer.valueOf(dateFields[0]), Integer.valueOf(dateFields[1]), Integer.valueOf(dateFields[2]));
                  Long daysSinceEpoch = ChronoUnit.DAYS.between(epoch, eventDate);

                  // create date Node - also keep track of the date has already been added
                  if (!dates.contains(daysSinceEpoch)) {
                     final Node dateNode = new Node(daysSinceEpoch.toString(), "date");
                     dateNode.addAttribute("dateString", fields[0]);
                     elements.add(dateNode);
                     dates.add(daysSinceEpoch);
                  }

                  // create stockSymbolOnDate Edge
                  Float dayOpen = Float.valueOf(fields[1]);
                  Float dayHigh = Float.valueOf(fields[2]);
                  Float dayLow = Float.valueOf(fields[3]);
                  Float dayClose = Float.valueOf(fields[4]);
                  Float dayAdjClose = Float.valueOf(fields[5]);
                  Integer dayVolume = Integer.valueOf(fields[6]);
                  String edgeKey = symbol + "_" + daysSinceEpoch.toString();
                  Edge edge = new Edge(edgeKey, stockNode.getKey(), daysSinceEpoch.toString(), "stockSymbol", "date", "stockOnDate");
                  edge.addAttribute("dayOpen", dayOpen);
                  edge.addAttribute("dayHigh", dayHigh);
                  edge.addAttribute("dayLow", dayLow);
                  edge.addAttribute("dayClose", dayClose);
                  edge.addAttribute("dayAdjClose", dayAdjClose);
                  edge.addAttribute("dayVolume", dayVolume);
                  elements.add(edge);
               }
            } catch (Exception e) {
               // sometimes input lines are dirty. skip and continue.
               logger.error("Error in file '" + file.getAbsolutePath() + "' at line number " + lineNum + ".");
            }
         }

         // upsert for each input file
         App.getDataGraph().upsert(elements);

         scanner.close();

      }
   }
}
