package org.abyeti.footinfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
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

    FootDB() throws Exception {
        WebResource server = Client.create().resource(SERVER_ROOT);
        ClientResponse resp = server.get(ClientResponse.class);

        int serverStatus = resp.getStatus();

        System.out.println("Server Returned Status Code : "+serverStatus);
        if(serverStatus != 200)
            throw new Exception("Could not connect to server");
        else
            System.out.println("Connected to server");
    }

    URI createNode() {
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

    void addTeam(String teamName, String[] players) {
        URI teamNode = createNode();

        Map<String, String> data = new TreeMap<>();
        data.put("TEAM-NAME", teamName);

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
            playerData.put("NAME", player);

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

    boolean createGame(String tA, String tB) {
        boolean status = true;

        WebResource res = null;
        ClientResponse response = null;

        JSONObject teamAData,teamBData;

        tA = new StringBuilder("\"").append(tA).append("\"").toString();
        tB = new StringBuilder("\"").append(tB).append("\"").toString();

        try {
            URI getTeamAURI = new URI(new URIBuilder(GET_TEAM_BY_LABEL_AND_NAME).addParameter("TEAM-NAME", tA).toString());
            URI getTeamBURI = new URI(new URIBuilder(GET_TEAM_BY_LABEL_AND_NAME).addParameter("TEAM-NAME", tB).toString());

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
            addRelationship(nodeA, nodeB, Relationships.VERSUS, new JSONObject().append("UNIQUE-ID", UUID.randomUUID().toString()).toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            status = false;
        }

        return status;
    }
}