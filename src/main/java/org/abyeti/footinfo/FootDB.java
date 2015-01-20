package org.abyeti.footinfo;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class FootDB {
    GraphDatabaseService db = null;

    private static final String DATABASE_URL = "target/NEODB";

    FootDB(){
        db = new GraphDatabaseFactory().newEmbeddedDatabase(DATABASE_URL);
    }
    public enum Labels implements Label {
        PLAYER, TEAM
    }
    private static enum Relationships implements RelationshipType {
        PLAYED_FOR, SCORED_GOAL_IN
    }

    void addTeam(String team, String[] players) {
        try(Transaction tx = db.beginTx()){
            Node nteam = db.createNode(Labels.TEAM);
            nteam.setProperty("NAME", team);
            for(String player : players) {
                Node nplayer = db.createNode(Labels.PLAYER);
                nplayer.setProperty("NAME", player);
                nteam.createRelationshipTo(nplayer, Relationships.PLAYED_FOR);
            }
            tx.success();
        }
    }
}