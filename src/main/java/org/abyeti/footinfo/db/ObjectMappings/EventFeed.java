package org.abyeti.footinfo.db.ObjectMappings;

/**
 * Created by Work on 1/27/2015.
 */
public class EventFeed {
    private int id;
    private String matchId;
    private String eventText;

    public EventFeed(String matchId, String eventText) {
        this.matchId = matchId;
        this.eventText = eventText;
    }

    public int getId() {
        return id;
    }

    public String getMatchId() {
        return  matchId;
    }

    public String getEventText() {
        return eventText;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
}
