package org.abyeti.footinfo;

import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Created by Work on 1/20/2015.
 */
public class FootDB {
    private static final String DATABSE_PATH = "target/neo-db";

    GraphDatabaseService graphDB = null;
    Relationship rel = null;

    private static enum Relationsips implements RelationshipType {
        IS_A_MEMBER_OF
    }
    FootDB() {

        deleteFileOrDirectory(new File(DATABSE_PATH));
        graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(DATABSE_PATH);
        registerShutdownHook(graphDB);
        try {
            Transaction tx = graphDB.beginTx();
            String[] players = {"a", "b", "c", "d"};

            Node team = graphDB.createNode();
            team.setProperty("Team Name", "AJAX");

            for(String s : players) {
                Node n = graphDB.createNode();
                n.setProperty("Name", s);
                rel = team.createRelationshipTo(n, Relationsips.IS_A_MEMBER_OF);
                rel.setProperty("Is a Member", "CLUB");
            }
            tx.success();
            graphDB.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void registerShutdownHook(final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }

    private static void deleteFileOrDirectory( File file ) {
        if ( file.exists() ) {
            if ( file.isDirectory() ) {
                for ( File child : file.listFiles() ) {
                    deleteFileOrDirectory( child );
                }
            }
            file.delete();
        }
    }
}
