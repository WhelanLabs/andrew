package com.whelanlabs.andrew;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.whelanlabs.andrew.dataset.Dataset;
import com.whelanlabs.andrew.dataset.DateUtils;
import com.whelanlabs.andrew.process.evaluate.Evaluation;
import com.whelanlabs.andrew.process.evaluate.Evaluator;
import com.whelanlabs.andrew.process.selection.AveragePercentageScoringMachine;
import com.whelanlabs.andrew.process.selection.ScoringMachine;
import com.whelanlabs.andrew.process.selection.ThoughtScore;
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

   public static void train(Goal goal, LocalDate startDate, LocalDate endDate, Map<String, List<Object>> trainingParameters, TrainingCriteria trainingCriteria) throws Exception {
      
      List<Thought> currentThoughts = new ArrayList<>();
      currentThoughts.addAll(goal.getThoughts());

      ScoringMachine scoringMachine = new AveragePercentageScoringMachine();
      Crossover crossover = new SimpleCrossover();
      
      // repeat
      Integer i = 0;
      do {
         Map<String, Object> iterationParameters = goal.setTrainingParameters(trainingParameters);
         
         i++;
         // generate mutants
         currentThoughts.addAll(mutator.createMutant(currentThoughts, 1));

         // generate crossover
         currentThoughts.addAll(crossover.createCrossovers(currentThoughts));
         
         logger.debug("currentThoughts.size() = " + currentThoughts.size());
         
         // loop through a set of test cases
         Evaluator evaluator = new Evaluator(goal.getNode());
         List<Evaluation> evualationResults = evaluator.evaluateThoughts2(trainingCriteria, iterationParameters);
         logger.debug("evualationResults = " + evualationResults);

         
         // sum the score for each thought
         List<ThoughtScore> scores = scoringMachine.scoreAndRank(evualationResults);

         logger.debug("scores = " + scores);
         // cull the herd of thought/goal when limited for resources.
         // Have culling be statistical some sometimes bad thoughts survive.

         // until things don't get better (end of repeat-until)
      } while (i<= trainingCriteria.getNumGenerations());

      // write the results

   }
}
