package org.abyeti.footinfo;

import org.lightcouch.CouchDbClient;

/**
 * Created by Work on 1/22/2015.
 */
public class couchDB {
    couchDB() {
        System.out.println("Starting DB instance");
        CouchDbClient client = new CouchDbClient();
    }
}
