package org.abyeti.footinfo.db;

import org.abyeti.footinfo.db.ObjectMappings.*;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Blob;
import java.util.*;

public class DataDB {
    private SessionFactory sf;
    private Session session;
    private Transaction tx;

    // Add data to BD
    public void addFoul(String cB, String fO, String fT, String mId) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();
            tx = session.beginTransaction();

            session.save(new MatchFoul(cB, fO, fT, mId));

            tx.commit();
            session.close();
            sf.close();
            FootDB db = new FootDB();
            createEntryInLog(mId, String.format("%s fould %s at %s", db.getPlayerName(cB), db.getPlayerName(fO), Minutes.minutesBetween(new DateTime(db.getStartTime(mId)), new DateTime(fT))), "match_foul");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addGoal(String goalType, String scoredBy, String goalTime, String matchId, String teamId) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new MatchGoal(goalType, scoredBy, goalTime, matchId, teamId));
            tx.commit();

            session.close();
            sf.close();
            FootDB db = new FootDB();
            createEntryInLog(matchId, String.format("%s scored a %s at %s for %s.", db.getPlayerName(scoredBy), goalType, goalTime, db.getTeamName(teamId)), "player_goal");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlayerCard(String awardedTo, String cardType, String matchId) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            System.out.println(awardedTo+cardType+matchId);
            session.save(new PenaltyCard(awardedTo, cardType, matchId));
            tx.commit();

            session.close();
            sf.close();
            FootDB db = new FootDB();
            createEntryInLog(matchId, String.format("%s was awarded %s card.",db.getPlayerName(awardedTo), cardType), String.format("%s_player_card", cardType));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(String playerId, String dob, String country, Blob picture) throws Exception {
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

    public JSONArray getPlayerStats(String playerId) throws Exception {
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

    public void createEntryInLog(String matchId, String text, String eventType) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new EventFeed(matchId, text, eventType));
            tx.commit();

            session.close();
            sf.close();
            String[] participants = new FootDB().getParticipants(matchId);
            UserDB.notifySubscribers(String.format("%s vs %s", participants[0], participants[1]), text);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Map<String, String> getGameVerdict(String matchId) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();

            final String getGoals = String.format("SELECT team_id FROM match_goal WHERE match_id=\'%s\';", matchId);

            session = sf.openSession();
            tx = session.beginTransaction();

            Map<String, Integer> goalCount = new LinkedHashMap<>();

            for(Object row : session.createSQLQuery(getGoals).list()) {
                if(!goalCount.containsKey(row.toString()))
                    goalCount.put(row.toString(), 1);
                else
                    goalCount.put(row.toString(), goalCount.get(row.toString()) + 1);
            }

            FootDB db = new FootDB();

            Map<String, String> verdict = new LinkedHashMap<>();

            verdict.put("status", "succeeded");
            String[] participants = db.getParticipants(matchId);

            String teamAId = participants[0];
            String teamBId = participants[1];

            // Match has no events
            if(!goalCount.containsKey(teamAId))
                goalCount.put(teamAId, 0);

            if(!goalCount.containsKey(teamBId))
                goalCount.put(teamBId, 0);


            if(goalCount.get(teamAId) > goalCount.get(teamBId)) {
                verdict.put("win", db.getTeamName(teamAId));
                verdict.put("loss", db.getTeamName(teamBId));
                verdict.put("win_goals", goalCount.get(teamAId).toString());
                verdict.put("loss_goals", goalCount.get(teamBId).toString());
            }

            else if(goalCount.get(teamAId) == goalCount.get(teamBId)) {
                verdict.put("team_a", db.getTeamName(teamAId));
                verdict.put("team_b", db.getTeamName(teamBId));
                verdict.put("tie", goalCount.get(participants[0]).toString());
            }

            else {
                verdict.put("win", db.getTeamName(teamAId));
                verdict.put("loss", db.getTeamName(teamBId));
                verdict.put("win_goals", goalCount.get(teamAId).toString());
                verdict.put("loss_goals", goalCount.get(teamBId).toString());
            }

            tx.commit();
            session.close();

            return verdict;
        }
        catch (Exception e){
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    public JSONObject getMatchFeed(String matchId) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            final String getMatchFeed = String.format("SELECT event_text, event_type FROM event_feed WHERE match_id=\'%s\' ORDER BY id DESC;", matchId);

            JSONObject matchFeed = new JSONObject();
            FootDB db = new FootDB();

            String[] participants = db.getParticipants(matchId);

            matchFeed.put("team_a", db.getTeamName(participants[0]));
            matchFeed.put("team_b", db.getTeamName(participants[1]));

            for(Object row : session.createSQLQuery(getMatchFeed).list()) {
                Object[] columns = (Object[]) row;
                matchFeed.accumulate("events",
                        new JSONObject().put("event_text", columns[0].toString())
                                .put("event_type", columns[1].toString())
                );
            }
            tx.commit();

            session.close();
            sf.close();

            System.out.println(matchFeed.toString());
            return matchFeed;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getTopFeed() throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            final String getMatchFeed = String.format("SELECT event_text, event_type, match_id FROM event_feed ORDER BY id DESC;");

            JSONArray matchFeed = new JSONArray();

            FootDB db = new FootDB();

            for(Object row : session.createSQLQuery(getMatchFeed).list()) {
                Object[] columns = (Object[]) row;
                String[] participants = db.getParticipants(columns[2].toString());
                String team_a = db.getTeamName(participants[0]);
                String team_b = db.getTeamName(participants[1]);
                matchFeed.put(
                        new JSONObject().put("event_text", columns[0].toString())
                                .put("event_type", columns[1].toString())
                                .put("match_id", columns[2].toString())
                                .put("team_a", team_a)
                                .put("team_b", team_b)
                );
            }
            tx.commit();

            session.close();
            sf.close();

            System.out.println(matchFeed.toString());
            return matchFeed;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkCodeValidity(String code) throws Exception {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            final String getCurrentAuthCode = "SELECT auth_code from auth_codes WHERE ORDER BY ID DESC LIMIT 1";

            Object row = session.createSQLQuery(getCurrentAuthCode).list();
            Object[] columns = (Object[]) row;

            tx.commit();
            session.close();
            sf.close();

            if(columns[0].toString().equals(code))
                return true;

            else
                return false;
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String addNewAuthCode() throws  Exception {
        try {
            String authCode = UUID.randomUUID().toString();
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new AuthCodes(authCode));

            tx.commit();
            session.close();
            sf.close();

            return authCode;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

}
