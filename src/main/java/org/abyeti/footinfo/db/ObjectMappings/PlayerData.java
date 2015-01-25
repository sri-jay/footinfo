package org.abyeti.footinfo.db.ObjectMappings;

import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;

/**
 * Created by Work on 1/25/2015.
 */
public class PlayerData {
    private int id;
    private String firstName;
    private String lastName;
    private String playerId;
    private String dob;
    private String country;

    public PlayerData(String firstName, String lastName, String playerId, String dob, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.playerId = playerId;
        this.dob = dob;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName(){
        return lastName;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName =firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
