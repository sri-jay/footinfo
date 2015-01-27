package org.abyeti.footinfo.db.ObjectMappings;

import javax.persistence.Entity;

/**
 * Created by Work on 1/25/2015.
 * For hibernate mapping
 */
@Entity
public class MatchFoul {
    private int id;
    private String commitedBy;
    private String foulOn;
    private String foulTime;
    private String matchId;

    public MatchFoul(String commitedBy, String foulOn, String foulTime, String matchId) {
        this.commitedBy = commitedBy;
        this.foulOn = foulOn;
        this.foulTime = foulTime;
        this.matchId = matchId;
    }

    public int getId() {
       return id;
    }

    public String getFoulOn() {
        return foulOn;
    }

    public String getCommitedBy() {
        return commitedBy;
    }

    public String getFoulTime() {
        return foulTime;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setId(int idnum) {
        id = idnum;
    }

    public void setCommitedBy(String cB) {
        commitedBy = cB;
    }

    public void setFoulOn(String fO) {
        foulOn = fO;
    }

    public void setFoulTime(String fT) {
        foulTime = fT;
    }

    public void  setMatchId(String mid) {
        matchId = mid;
    }
}
