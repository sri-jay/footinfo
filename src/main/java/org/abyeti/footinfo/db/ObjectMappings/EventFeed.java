package org.abyeti.footinfo.db.ObjectMappings;

/**
 * Created by Work on 1/27/2015.
 */
public class EventFeed {
    private int id;
    private String matchId;
    private String eventText;
    private String eventType;

    public EventFeed(String matchId, String eventText, String eventType) {
        this.matchId = matchId;
        this.eventText = eventText;
        this.eventType = eventType;
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

    public String getEventType() { return eventType; }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }

    public void setEventType(String eventType) { this.eventType = eventType; }
}
