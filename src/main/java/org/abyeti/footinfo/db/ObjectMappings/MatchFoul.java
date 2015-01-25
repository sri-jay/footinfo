package org.abyeti.footinfo.db.ObjectMappings;

/**
 * Created by Work on 1/25/2015.
 * For hibernate mapping
 */
public class MatchFoul {
    private int id;
    private String commitedBy;
    private String foulOn;
    private String foulTime;
    private String matchId;

    public MatchFoul(String cB, String fO, String fT, String mId) {
        commitedBy = cB;
        foulOn = fO;
        foulTime = fT;
        matchId = mId;
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
