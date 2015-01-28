package org.abyeti.footinfo.db.ObjectMappings;

import org.hibernate.annotations.Table;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;

import javax.persistence.Entity;

/**
 * Created by Work on 1/25/2015.
 */
@Entity
public class MatchGoal {
    private int id;
    private String goalType;
    private String scoredBy;
    private String goalTime;
    private String matchId;
    private String teamId;

    public MatchGoal(String goalType, String scoredBy, String goalTime, String matchId, String teamId) {
        this.goalType = goalType;
        this.scoredBy = scoredBy;
        this.goalTime = goalTime;
        this.matchId = matchId;
        this.teamId = teamId;
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

    public String getTeamId() { return teamId; }

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

    public void setTeamId(String teamId) { this.teamId = teamId; }
}
