package org.abyeti.footinfo.db.ObjectMappings;

/**
 * Created by Work on 2/4/2015.
 */
public class AuthCodes {
    private int id;
    private String authCode;


    public AuthCodes(String authCode) {
        this.authCode = authCode;
    }

    int getId() { return id; }
    void setId(int id) { this.id = id; }

    String getAuthCode() { return  authCode; }
    void setAuthCode(String authCode) { this.authCode = authCode; }
}
