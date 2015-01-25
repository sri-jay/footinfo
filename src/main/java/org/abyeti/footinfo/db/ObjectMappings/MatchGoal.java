package org.abyeti.footinfo.db.ObjectMappings;

import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;

/**
 * Created by Work on 1/25/2015.
 */
public class MatchGoal {
    private int id;
    private String goalType;
    private String scoredBy;
    private String goalTime;
    private String matchId;

    public MatchGoal(String gT, String sB, String gTime, String mId) {
        goalType = gT;
        scoredBy = sB;
        goalTime = gTime;
        matchId = mId;
    }

    public int getId() {
        return id;
    }

    public String getGoalType() {
        return goalType;
    }

    public String getScoredBy() {
        return scoredBy;
    }

    public String getGoalTime() {
        return goalTime;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public void setScoredBy(String scoredBy) {
        this.scoredBy = scoredBy;
    }

    public void setGoalTime(String goalTime) {
        this.goalTime = goalTime;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
