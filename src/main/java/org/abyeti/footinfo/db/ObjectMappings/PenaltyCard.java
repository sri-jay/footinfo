package org.abyeti.footinfo.db.ObjectMappings;

/**
 * Created by Work on 1/25/2015.
 */
public class PenaltyCard {
    private int id;
    private String awardedTo;
    private String cardType;
    private String matchId;

    public PenaltyCard(String awardedTo, String cardType, String matchId) {
        this.awardedTo = awardedTo;
        this.cardType = cardType;
        this.matchId = matchId;
    }

    public int getId() {
        return id;
    }

    public String getAwardedTo() {
        return awardedTo;
    }

    public String getCardType() {
        return cardType;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAwardedTo(String awardedTo) {
        this.awardedTo = awardedTo;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
