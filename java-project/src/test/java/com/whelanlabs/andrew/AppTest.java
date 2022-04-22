package com.whelanlabs.andrew;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.whelanlabs.kgraph.engine.KnowledgeGraph;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
    * @throws Exception 
     */
    @Test
    public void getKGraphConnection_valid_success() throws Exception
    {
       String databaseName = "andrew_test_database";
       App app = new App(databaseName);
       Long graphCount = app.getGraph().getTotalCount();
        assert( 0 == graphCount ): "graphCount = " + graphCount;
    }
}
