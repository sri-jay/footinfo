package org.abyeti.footinfo.db;

import org.abyeti.footinfo.db.ObjectMappings.MatchFoul;
import org.abyeti.footinfo.db.ObjectMappings.MatchGoal;
import org.abyeti.footinfo.db.ObjectMappings.PenaltyCard;
import org.abyeti.footinfo.db.ObjectMappings.PlayerData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * Created by Work on 1/25/2015.
 */
public class DataDB {
    private static SessionFactory sf;
    private static Session session;
    private static Transaction tx;

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
            session.save(new PenaltyCard(awardedTo, cardType, matchId));
            tx.commit();

            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPlayer(String firstName, String lastName, String playerId, String dob, String country) {
        try {
            sf = new Configuration().configure().buildSessionFactory();
            session = sf.openSession();

            tx = session.beginTransaction();
            session.save(new PlayerData(firstName, lastName, playerId, dob, country));
            tx.commit();

            session.close();
            sf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
