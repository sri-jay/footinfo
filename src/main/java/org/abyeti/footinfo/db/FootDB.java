package org.abyeti.footinfo.db;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import org.neo4j.graphdb.RelationshipType;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class FootDB {
    private final String SERVER_ROOT = "http://127.0.01:7474/db/data";
    private final String NODE_NOOT = "http://127.0.0.1:7474/db/data/node";

    private final String RAW_CIPHER_AUTOCOMMIT = "http://127.0.0.1:7474/db/data/transaction/commit";

    private final String GET_TEAM_BY_LABEL_AND_NAME = "http://127.0.0.1:7474/db/data/label/TEAM/nodes";

    private enum Relationships implements RelationshipType {
        PLAYS_FOR, VERSUS, LOST_TO
    }

    private enum NodeLabels {
        PLAYER, TEAM, MATCH
    }

    public FootDB() throws Exception {
        WebResource server = Client.create().resource(SERVER_ROOT);
        ClientResponse resp = server.get(ClientResponse.class);

        int serverStatus = resp.getStatus();

        System.out.println("Server Returned Status Code : "+serverStatus);
        if(serverStatus != 200)
            throw new Exception("Could not connect to server");
        else
            System.out.println("Connected to server");
    }

    public URI createNode() {
        WebResource nodeResource = Client.create().resource(NODE_NOOT);
        ClientResponse resp = nodeResource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("{}")
                .post(ClientResponse.class);

        final URI nodeEndpoint = resp.getLocation();
        return nodeEndpoint;
    }

    void addPropertyToNode(URI nodeEndpoint, Map<String, String> properties) {
        WebResource resource = null;
        ClientResponse resp = null;

        for(Map.Entry<String, String> ent : properties.entrySet()) {
            String nodePropertyURI = nodeEndpoint.toString() + "/properties/" + ent.getKey();
            resource = Client.create().resource(nodePropertyURI);
            resp = resource
                    .accept(MediaType.APPLICATION_JSON)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\""+ent.getValue()+"\"")
                    .put(ClientResponse.class);
            resp.close();
        }
    }

    URI addRelationship(URI startNode, URI targetNode, Relationships relationShipType, String jsonAttributes) throws URISyntaxException {
        URI startNodeRel = new URI(startNode.toString() + "/relationships");

        // Creating serverside resource to add relationships
        WebResource res = Client.create().resource(startNodeRel);
        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .entity(generateJSONRelationship(targetNode, relationShipType, jsonAttributes))
                .post(ClientResponse.class);
        final URI relationshipLocation = response.getLocation();
        response.close();

        return relationshipLocation;
    }

    String generateJSONRelationship(URI targetNode, Relationships relationshipType, String... jsonAttributes) {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append(targetNode.toString());
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
            sb.append( "\"" );

        else  {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ ) {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                    sb.append( ", " );
            }
        }

        sb.append( " }" );
        return sb.toString();
    }

    void addLabelToNode(URI node, NodeLabels label) throws URISyntaxException {
        URI lnode = new URI(node.toString() + "/labels");
        WebResource res = Client.create().resource(lnode);

        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .entity("[\"" + label + "\"]")
                .post(ClientResponse.class);

        if(response.getStatus() != 204)
            System.out.println("Bad Request!\nCould not add label to node");
    }

    // Football functions

    public void addTeam(String teamName, String[] players) throws Exception {
        URI teamNode = createNode();

        Map<String, String> data = new TreeMap<>();
        data.put("team_name", teamName);
        data.put("team_id", UUID.randomUUID().toString());

        addPropertyToNode(teamNode, data);
        try{
            addLabelToNode(teamNode, NodeLabels.TEAM);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for(String player : players){
            URI playerNode = createNode();
            Map<String, String> playerData = new TreeMap<String, String>();
            playerData.put("name", player);
            playerData.put("player_id", UUID.randomUUID().toString());

            addPropertyToNode(playerNode, playerData);
            try {
                addLabelToNode(playerNode, NodeLabels.PLAYER);
                addRelationship(playerNode, teamNode, Relationships.PLAYS_FOR, "{}");
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean createGame(String tA, String tB) throws Exception {
        boolean status = true;

        WebResource res = null;
        ClientResponse response = null;

        JSONObject teamAData,teamBData;

        tA = new StringBuilder("\"").append(tA).append("\"").toString();
        tB = new StringBuilder("\"").append(tB).append("\"").toString();

        try {
            URI getTeamAURI = new URI(new URIBuilder(GET_TEAM_BY_LABEL_AND_NAME).addParameter("team_name", tA).toString());
            URI getTeamBURI = new URI(new URIBuilder(GET_TEAM_BY_LABEL_AND_NAME).addParameter("team_name", tB).toString());

            System.out.println(getTeamAURI.toString());
            res = Client.create().resource(getTeamAURI);
            response = res
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            if (response.getStatus() != 200)
                throw new Exception("TEAM_NOT_FOUND");

            System.out.println(response.getStatus());
            teamAData = new JSONArray(response.getEntity(String.class)).getJSONObject(0);

            res = Client.create().resource(getTeamBURI);
            response = res
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);

            System.out.println(response.getStatus());
            if (response.getStatus() != 200)
                throw new Exception("TEAM_NOT_FOUND");

            teamBData = new JSONArray(response.getEntity(String.class)).getJSONObject(0);
            response.close();

            // Get team B data
            URI nodeA = new URI(teamAData.getString("self"));
            URI nodeB = new URI(teamBData.getString("self"));

            // The Relation between teams A and B is now A vs B, i.e the match is ongoing
            addRelationship(nodeA, nodeB, Relationships.VERSUS, new JSONObject()
                            .append("match_id", UUID.randomUUID().toString())
                            .append("match_status", "ongoing")
                            .append("start_time", new DateTime().toString())
                            .toString()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
            status = false;
        }

        return status;
    }

    public JSONArray getGameStatuses() throws Exception {
        JSONArray gameStatuses = new JSONArray();

        final String cipherQuery = "{ \"statements\" : [{ \"statement\" : \"match (a)-[r:VERSUS]->(b) return a,b,r\" }]}";

        WebResource res = Client.create().resource(RAW_CIPHER_AUTOCOMMIT);
        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .entity(cipherQuery)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        JSONArray jsonDump = new JSONObject(response.getEntity(String.class)).getJSONArray("results").getJSONObject(0).getJSONArray("data");

        for(int i=0;i<jsonDump.length();i++) {
            JSONArray row = jsonDump.getJSONObject(i).getJSONArray("row");
            JSONArray matchId = row.getJSONObject(2).getJSONArray("match_id");
            JSONArray matchStatus = row.getJSONObject(2).getJSONArray("match_status");
            JSONArray matchStartTime = row.getJSONObject(2).getJSONArray("start_time");

            if(matchStatus.getString(0).equals("ongoing")) {
                JSONObject dat = new JSONObject()
                        .accumulate("match_participant_a", row.getJSONObject(0).getString("team_name"))
                        .accumulate("match_participant_b", row.getJSONObject(1).getString("team_name"))
                        .accumulate("match_id", matchId.getString(0))
                        .accumulate("start_time", matchStartTime.getString(0))
                        .accumulate("match_status", matchStatus.getString(0));
                gameStatuses.put(dat);
            }
        }
        System.out.println(gameStatuses.toString());
        return gameStatuses;
    }

    public JSONArray getTeamData(String teamId) throws Exception {
        JSONArray players = new JSONArray();

        final String cipherQuery = String.format("{ \"statements\" : [{ \"statement\" : \"match (a)-[r:PLAYS_FOR]->(b) where b.team_id=\'%s\' return a,b\"}] }", teamId);

        WebResource res = Client.create().resource(RAW_CIPHER_AUTOCOMMIT);

        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .entity(cipherQuery)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);

        String data = response.getEntity(String.class);
        System.out.println(data);
        JSONArray jsonDump = new JSONObject(data).getJSONArray("results").getJSONObject(0).getJSONArray("data");

        for(int i=0;i<jsonDump.length();i++) {
            JSONArray row = jsonDump.getJSONObject(i).getJSONArray("row");
            JSONObject dat = new JSONObject()
                    .accumulate("player_name", row.getJSONObject(0).getString("name"))
                    .accumulate("player_id", row.getJSONObject(0).getString("player_id"))
                    .accumulate("club_name", row.getJSONObject(1).getString("team_name"));

            players.put(dat);
        }

        return players;
    }

    public JSONArray getAllClubs() throws Exception {
        JSONArray clubs = new JSONArray();

        final String cipherQuery = "{ \"statements\" : [{ \"statement\" : \"match (a)-[r:PLAYS_FOR]->(b) return distinct b\"}] }";

        WebResource res = Client.create().resource(RAW_CIPHER_AUTOCOMMIT);
        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(cipherQuery)
                .post(ClientResponse.class);

        JSONArray jsonDump = new JSONObject(response.getEntity(String.class)).getJSONArray("results").getJSONObject(0).getJSONArray("data");

        System.out.println(jsonDump.toString());
        for(int i=0;i<jsonDump.length();i++) {
            JSONArray row = jsonDump.getJSONObject(i).getJSONArray("row");
            JSONObject dat = new JSONObject()
                    .accumulate("team_name", row.getJSONObject(0).getString("team_name"))
                    .accumulate("team_id", row.getJSONObject(0).getString("team_id"));

            clubs.put(dat);
        }
        return clubs;
    }

    public boolean stopGame(String matchId) throws Exception {
        final String cipherQuery = String.format("{\"statements\" : [{\"statement\" : \"match (a)-[r:VERSUS]->(b) where r.match_id=[\'%s\'] return id(r)\"}] }", matchId);

        System.out.println(cipherQuery);

        WebResource res = Client.create().resource(RAW_CIPHER_AUTOCOMMIT);

        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(cipherQuery)
                .post(ClientResponse.class);

        String data = response.getEntity(String.class);

        int id = new JSONObject(data).getJSONArray("results").getJSONObject(0).getJSONArray("data").getJSONObject(0).getJSONArray("row").getInt(0);

        final String finishGame = String.format("http://localhost:7474/db/data/relationship/%d/properties/match_status", id);

        res = Client.create().resource(finishGame);
        response = res
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("[\"finished\"]")
                .put(ClientResponse.class);

        if(response.getStatus() == 204){
            return true;
        }

        return false;
    }

    public JSONObject getGameData(String matchId) throws Exception {
        final String cipherQuery = String.format("{ \"statements\" : [{ \"statement\" : \"match (a)-[r:VERSUS]->(b) where r.match_id=[\'%s\'] return a.team_id,b.team_id \" }] }", matchId);
        WebResource res = Client.create().resource(RAW_CIPHER_AUTOCOMMIT);
        ClientResponse response = res
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(cipherQuery)
                .post(ClientResponse.class);

        String responseData = response.getEntity(String.class);

        System.out.println(responseData);

        JSONArray row  = new JSONObject(responseData).getJSONArray("results").getJSONObject(0).getJSONArray("data").getJSONObject(0).getJSONArray("row");

        System.out.println(row.toString());

        JSONArray teamAData = getTeamData(row.getString(0));
        JSONArray teamBData = getTeamData(row.getString(1));

        JSONObject data = new JSONObject().accumulate("team_a", teamAData).accumulate("team_b", teamBData).accumulate("status", "succeeded");

        System.out.println(data.toString());
        return data;
    }
}