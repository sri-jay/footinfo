package org.abyeti.footinfo.db.ObjectMappings;

import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;

import java.sql.Blob;

/**
 * Created by Work on 1/25/2015.
 */
public class PlayerData {
    private int id;
    private String playerId;
    private String dob;
    private String country;
    private Blob picture;

    public PlayerData(String playerId, String dob, String country, Blob picture) {
        this.playerId = playerId;
        this.dob = dob;
        this.country = country;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getDob() {
        return dob;
    }

    public String getCountry() {
        return country;
    }

    public Blob getPicture() {
        return picture;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerId(String playerId){
        this.playerId = playerId;
    }

    public void  setDob(String dob){
        this.dob = dob;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }
}
