package org.abyeti.footinfo.db;

import org.abyeti.footinfo.db.ObjectMappings.MatchFoul;
import org.abyeti.footinfo.db.ObjectMappings.MatchGoal;
import org.abyeti.footinfo.db.ObjectMappings.PenaltyCard;
import org.abyeti.footinfo.db.ObjectMappings.PlayerData;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Blob;
import java.util.*;

/**
 * Created by Work on 1/25/2015.
 */
public class DataDB {
    private static SessionFactory sf;
    private static Session session;
    private static Transaction tx;

    // Add data to BD
    public static void addFoul(String cB, String fO, String fT, String mId) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();
            tx = session.beginTransaction();

            session.save(new MatchFoul(cB, fO, fT, mId));

            tx.commit();
            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addGoal(String goalType, String scoredBy, String goalTime, String matchId) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new MatchGoal(goalType, scoredBy, goalTime, matchId));
            tx.commit();

            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerCard(String awardedTo, String cardType, String matchId) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            System.out.println(awardedTo+cardType+matchId);
            session.save(new PenaltyCard(awardedTo, cardType, matchId));
            tx.commit();

            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPlayer(String playerId, String dob, String country, Blob picture) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new PlayerData(playerId, dob, country, picture));
            tx.commit();

            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getPlayerStats(String playerId) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();

            // Queries to get the data to generate a player summary.

            String fSql = String.format("SELECT foul_on, foul_time, match_id FROM match_foul WHERE commited_by = \'%s\' ORDER BY id;", playerId);
            String gSql = String.format("SELECT goal_type, goal_time, match_id FROM match_goal WHERE scored_by = \'%s\' ORDER BY id;", playerId);
            String pSql = String.format("SELECT card_type, match_id FROM penalty_card WHERE awarded_to =\'%s\' ORDER BY id DESC;", playerId );

            int foulsCount = 0;
            int goalsCount = 0;
            int redCards = 0;
            int yellowCards = 0;

            Set<String> matches = new LinkedHashSet<String>();

            Map<String, Integer> pFouls = new LinkedHashMap<String, Integer>();
            Map<String, Integer> pGoals = new LinkedHashMap<String, Integer>();
            Map<String, Integer> yCards = new LinkedHashMap<String, Integer>();
            Map<String, Integer> rCards = new LinkedHashMap<String, Integer>();

            List<Object> rows = null;
            // Get the foul data first
            Query fouls = session.createSQLQuery(fSql);
            rows = fouls.list();
            for(Object row : rows) {
                Object[] columns = (Object[]) row;
                matches.add(columns[2].toString());
                if(!pFouls.containsKey(columns[2].toString()))
                    pFouls.put(columns[2].toString(), 0);

                pFouls.put(columns[2].toString(), pFouls.get(columns[2].toString())+1);
            }

            // Get the goal data
            Query goals = session.createSQLQuery(gSql);
            rows = goals.list();
            for(Object row : rows) {
                Object[] columns = (Object[]) row;
                matches.add(columns[2].toString());
                if(!pGoals.containsKey(columns[2].toString()))
                    pGoals.put(columns[2].toString(), 0);

                pGoals.put(columns[2].toString(), pGoals.get(columns[2].toString())+1);
            }


            // Get the cards data
            Query cards = session.createSQLQuery(pSql);
            rows = cards.list();
            for(Object row : rows) {
                Object[] columns = (Object[]) row;
                if(!yCards.containsKey(columns[1].toString()))
                    yCards.put(columns[1].toString(), 0);

                if(!rCards.containsKey(columns[1].toString()))
                    rCards.put(columns[1].toString(), 0);

                if(columns[0].toString().equals("yellow"))
                    yCards.put(columns[1].toString(), yCards.get(columns[1].toString()) + 1);
                else
                    rCards.put(columns[1].toString(), rCards.get(columns[1].toString())+1);
            }

            JSONArray playerData = new JSONArray();

            for(String matchId : matches) {
                if(!pGoals.containsKey(matchId))
                    pGoals.put(matchId, 0);
                if(!pFouls.containsKey(matchId))
                    pFouls.put(matchId, 0);
                if(!yCards.containsKey(matchId))
                    yCards.put(matchId, 0);
                if(!rCards.containsKey(matchId))
                    rCards.put(matchId, 0);
            }

            int i = 0;

            for(String matchId : matches){
                try {
                    String[] participants = new FootDB().getParticipants(matchId);
                    JSONObject matchData = new JSONObject();
                    matchData.accumulate(participants[0] + " vs " + participants[1], new JSONObject("{ \'goals\' : \'" + Integer.toString(pGoals.get(matchId)) + "\'}"));
                    matchData.accumulate(participants[0] + " vs " + participants[1], new JSONObject("{ \'fouls\' :\'" + Integer.toString(pFouls.get(matchId)) + "\' }"));
                    matchData.accumulate(participants[0] + " vs " + participants[1], new JSONObject("{ \'yellow_cards\' : \'" + Integer.toString(yCards.get(matchId)) + "\'}"));
                    matchData.accumulate(participants[0] + " vs " + participants[1], new JSONObject("{ \'red_cards\' : \'" + Integer.toString(rCards.get(matchId)) + "\'}"));
                    playerData.put(i++, matchData);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            System.out.println("PDAT : "+playerData.toString());

            tx.commit();

            session.close();
            sf.close();
            return playerData;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
