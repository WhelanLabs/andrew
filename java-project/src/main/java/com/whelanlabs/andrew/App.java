package com.whelanlabs.andrew;

import com.whelanlabs.kgraph.engine.KnowledgeGraph;

/**
 * Hello world!
 *
 */
public class App 
{
   private KnowledgeGraph _kGraph = null;

   public App(String databaseName) throws Exception {
      _kGraph = new KnowledgeGraph(databaseName);
   }
   
   public KnowledgeGraph getGraph() {
      return _kGraph;
   }

}
