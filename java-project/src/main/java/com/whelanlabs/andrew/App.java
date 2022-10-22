package com.whelanlabs.andrew;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.whelanlabs.andrew.dataset.Dataset;
import com.whelanlabs.andrew.dataset.DateUtils;
import com.whelanlabs.andrew.process.Evaluation;
import com.whelanlabs.andrew.process.Evaluator;
import com.whelanlabs.andrew.process.ProcessUtils;
import com.whelanlabs.andrew.process.AveragePercentageScoringMachine;
import com.whelanlabs.andrew.process.ScoringMachine;
import com.whelanlabs.andrew.process.ThoughtScore;
import com.whelanlabs.kgraph.engine.Edge;
import com.whelanlabs.kgraph.engine.Element;
import com.whelanlabs.kgraph.engine.KnowledgeGraph;
import com.whelanlabs.kgraph.engine.Node;
import com.whelanlabs.kgraph.engine.QueryClause;

/**
 * The Class App.
 */
public class App {

   /** The data graph. */
   private static KnowledgeGraph _dataGraph = null;

   /** The garden graph. */
   private static KnowledgeGraph _gardenGraph = null;

   /** The logger. */
   private static Logger logger = LogManager.getLogger(App.class);

   private static Map<String, Thought> thoughtCache = new HashMap<>();

   private static Mutator mutator = new Mutator();

   private static ProcessUtils processUtils = new ProcessUtils();

   /**
    * Instantiates a new app.
    */
   private App() {
      // do nothing. Static global class.
   }

   /**
    * Initialize.
    *
    * @param databaseName the database name
    * @throws Exception the exception
    */
   public static void initialize(String databaseName) throws Exception {
      _dataGraph = new KnowledgeGraph(databaseName);
      _gardenGraph = new KnowledgeGraph(databaseName + "_garden");
      thoughtCache = new HashMap<>();
      mutator = new Mutator();
   }

   /**
    * Gets the data graph.
    *
    * @return the data graph
    */
   public static KnowledgeGraph getDataGraph() {
      return _dataGraph;
   }

   /**
    * Gets the garden graph.
    *
    * @return the garden graph
    */
   public static KnowledgeGraph getGardenGraph() {
      return _gardenGraph;
   }

   /**
    * Load dataset to data graph.
    *
    * @param dataset the dataset
    */
   public static void loadDatasetToDataGraph(Dataset dataset) {
      String datasetInfoID = dataset.getDatasetInfoID();
      QueryClause datasetInfoQuery = new QueryClause("dataset_id", QueryClause.Operator.EQUALS, datasetInfoID);
      List<Node> datasetInfo = getDataGraph().queryNodes("dataSet_info", datasetInfoQuery);
      if (datasetInfo.size() > 1) {
         throw new RuntimeException("Dataset " + datasetInfoID + " misloaded.");
      } else if (0 == datasetInfo.size()) {
         // load dataset nodes
         List<Node> nodes = dataset.getNodesToLoad();
         Node[] nodesArray = new Node[nodes.size()];
         nodesArray = nodes.toArray(nodesArray);
         _dataGraph.upsert(nodesArray);

         // load dataset edges
         List<Edge> edges = dataset.getEdgesToLoad();
         Edge[] edgesArray = new Edge[edges.size()];
         edgesArray = edges.toArray(edgesArray);
         _dataGraph.upsert(edgesArray);

         // lastly, create the dataSet_info node
         Node datasetInfoNode = new Node(datasetInfoID, "dataSet_info");
         datasetInfoNode.addAttribute("dataset_id", datasetInfoID);
         datasetInfoNode.addAttribute("max_time", dataset.getMaxTime());
         _dataGraph.upsert(datasetInfoNode);
      }
   }

   public static Thought loadThoughtFromJson(String thoughtName, String content) {
      if (thoughtCache.containsKey(thoughtName)) {
         return thoughtCache.get(thoughtName);
      } else {
         Thought thought = loadThoughtFromJson(content);
         thoughtCache.put(thoughtName, thought);
         return thought;
      }
   }

   /**
    * Load thought from json.
    *
    * @param content the content
    * @return the thought
    */
   public static Thought loadThoughtFromJson(String content) {
      JSONArray jsonArr = new JSONArray(content);
      Thought result = null;

      List<Element> loadedElements = _gardenGraph.loadFromJson(jsonArr);

      logger.debug("loadedElements = " + loadedElements);

      for (Element element : loadedElements) {
         String type = element.getType();
         logger.debug("type = " + type);
         if ("thought".equals(type)) {
            logger.debug("element = " + element);
            result = new Thought((Node) element);
         }
      }
      return result;
   }

   public static List<ThoughtScore> train(Goal goal, LocalDate startDate, LocalDate endDate, Map<String, List<Object>> trainingParameters,
         TrainingCriteria trainingCriteria) throws Exception {

      // File file = File.createTempFile("andrew_training_report_", ".txt");
      String path = "./target/report/";
      Files.createDirectories(Paths.get(path));
      File dir = new File(path);
      File file = new File(dir, "Andrew_training_report_" + System.currentTimeMillis() + ".txt");
      FileWriter reportWriter = new FileWriter(file);
      reportWriter.write("Andrew Training Report\n");
      reportWriter.write("----------------------\n");
      List<Float> dataList = new ArrayList<>();
      
      Map<String, Thought> currentThoughts = new HashMap<>();
      List<Thought> goalThoughts = goal.getThoughts();
      for (Thought goalThought : goalThoughts) {
         currentThoughts.put(goalThought.getKey(), goalThought);
      }

      ScoringMachine scoringMachine = new AveragePercentageScoringMachine();
      List<ThoughtScore> scores = new ArrayList<>();
      Crossover simpleCrossover = new SimpleCrossover();

      // repeat
      Integer currentGen = 0;
      do {
         
         Map<String, Object> iterationParameters = goal.setTrainingParameters(trainingParameters);

         currentGen++;

         logger.debug("train: processing generation " + currentGen);
         
         logger.debug("currentThoughts.size() = " + currentThoughts.size());

         // loop through a set of test cases
         Evaluator evaluator = new Evaluator(goal.getNode());
         List<Evaluation> evualationResults = evaluator.evaluateThoughts2(currentThoughts, trainingCriteria, iterationParameters);
         logger.debug("evualationResults = " + evualationResults);

         // sum the score for each thought
         scores.addAll(scoringMachine.scoreAndRank(evualationResults));

         logger.debug("scores = " + scores);
         // cull the herd of thought/goal when limited for resources.
         // Have culling be statistical some sometimes bad thoughts survive.
         Map<String, List<Float>> scoreGroupings = processUtils.getGroupingByThoughtKey(scores);

         Set<Thought> nextThoughts = new HashSet<>();
         Integer nextPopSize = 0;

         // carry forward the seed thoughts
         for (Thought t : currentThoughts.values()) {
            if (true == (Boolean) t.getThoughtNode().getAttribute("seedThought")) {
               nextThoughts.add(t);
               // generate mutant
               // Thought mutant = mutator.createMutant(t, 1);
               // nextThoughts.add(mutant);
               // nextPopSize += 2
               nextPopSize += 1;
            }
         }

         // carry forward the baby thoughts
         for (String groupKey : scoreGroupings.keySet()) {
            Integer age = scoreGroupings.get(groupKey).size();
            if (age < trainingCriteria.getMaturationAge()) {
               Thought babyKey = currentThoughts.get(groupKey);
               nextThoughts.add(babyKey);
               nextPopSize++;
            }
         }

         // carry forward the remaining best thoughts
         // TODO: make this statistical to sometimes allow bad thoughts to live
         Map<Float, List<String>> rankings = new HashMap<>();
         for (String scoreGroupingKey : scoreGroupings.keySet()) {
            if (scoreGroupings.get(scoreGroupingKey).size() >= trainingCriteria.getMaturationAge()) {
               Float average = calculateAverage(scoreGroupings.get(scoreGroupingKey));
               if (!rankings.containsKey(average)) {
                  List<String> thoughtIDs = new ArrayList<>();
                  thoughtIDs.add(scoreGroupingKey);
                  rankings.put(average, thoughtIDs);
               } else {
                  List<String> thoughtIDs = rankings.get(average);
                  thoughtIDs.add(scoreGroupingKey);
                  rankings.put(average, thoughtIDs);
               }
            }
         }
         ArrayList<Float> scoresList = new ArrayList<Float>(rankings.keySet());
         Collections.sort(scoresList, Collections.reverseOrder());
         for (Float score : scoresList) {
            if (nextPopSize >= trainingCriteria.getMaxPopulation()) {
               break;
            }
            List<String> values = rankings.get(score);
            for (String value : values) {
               if (currentThoughts.containsKey(value)) {
                  if (nextPopSize >= trainingCriteria.getMaxPopulation()) {
                     break;
                  }
                  Thought currentThought = currentThoughts.get(value);
                  // add the smart thought
                  nextThoughts.add(currentThought);
                  // add a smart offspring
                  Thought randomSpouse = getRandom(currentThoughts.values());
                  Thought child = simpleCrossover.crossover(currentThought, randomSpouse);
                  nextThoughts.add(child);
                  // generate mutants
                  Thought mutant = mutator.createMutant(currentThought, 1);
                  nextThoughts.add(mutant);
                  nextPopSize += 3;
               }

            }
         }

         Map<String, Thought> nextThoughtsMap = new HashMap<>();
         for (Thought nextThought : nextThoughts) {
            nextThoughtsMap.put(nextThought.getKey(), nextThought);
         }
         
         // report results from the current generation
         Float averageGenScore = getGenerationAverage(currentThoughts, scores);
         reportWriter.write("generation " + currentGen + " average score: \t" + averageGenScore + "\n");
         dataList.add(averageGenScore);
         
         currentThoughts = nextThoughtsMap;

         // until things don't get better (end of repeat-until)
      } while (currentGen < trainingCriteria.getNumGenerations());

      // see: https://stackoverflow.com/a/67370754/2418261
      SimpleRegression regression = new SimpleRegression();
      int dataListSize = dataList.size();
      double dataArray[][] = new double[dataListSize][2];
      for(int k = 0; k <dataListSize; k++) {
         dataArray[k][0] = k;
         dataArray[k][1] = dataList.get(k);
      }
      regression.addData(dataArray);
      regression.regress();
      reportWriter.write("\n" + "overall slope = " + regression.getSlope());
      reportWriter.close();
      
      return scores;
   }

   private static Float getGenerationAverage(Map<String, Thought> currentThoughts, List<ThoughtScore> scores) {
      Set<String> currentThoughtIDs = currentThoughts.keySet();
      int size = 0;
      float sum = 0f;
      for(ThoughtScore score : scores) {
         String thoughtKey = score.getThoughtKey();
         if(currentThoughtIDs.contains(thoughtKey)) {
            sum += score.getThoughtScore();
            size++;
         }
      }
      return sum/size;
   }

   public static Float calculateAverage(List<Float> scores) {
      Float sum = 0f;
      if (!scores.isEmpty()) {
         for (Float score : scores) {
            sum += score;
         }
         return sum / scores.size();
      }
      return sum;
   }

   public static <T> T getRandom(Collection<T> coll) {
      int num = (int) (Math.random() * coll.size());
      for (T t : coll)
         if (--num < 0)
            return t;
      throw new AssertionError();
   }
}
