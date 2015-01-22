package org.abyeti.footinfo;

import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class ClientApi {

    @POST
    @Path("/addTeam")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adminLogin(String entString) {
        JSONObject jsonData = new JSONObject(entString);
        Response resp = null;
        try {
            FootDB db = new FootDB();
            if (jsonData.getString("ACTION").equals("ADD_TEAM")) {
                String teamName = jsonData.getString("TEAM_NAME");
                String[] players = jsonData.getString("PLAYERS").split(";");
                db.addTeam(teamName, players);
                resp = Response.status(200).entity("{\"STATUS\" : \"SUCCEEDED\"}").build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            resp = Response.status(503).entity("{\"STATUS\" : \"FAILED\"}").build();
        }

        return resp;
    }

    @POST
    @Path("/createGame")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(String entString) {
        JSONObject jsonData = new JSONObject(entString);
        JSONObject responseData = new JSONObject();

        Response response = Response
                .status(200)
                .entity(responseData.append("STATUS", "SUCCEEDED").toString())
                .build();


        String teamA = jsonData.getString("TEAM_A");
        String teamB = jsonData.getString("TEAM_B");

        try {
            FootDB db = new FootDB();
            if(!db.createGame(teamA, teamB)) {
                Response
                        .status(404)
                        .entity(responseData.append("STATUS", "No Such Team!").toString())
                        .build();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Response
                    .status(502)
                    .entity(responseData.append("STATUS", "FAILED").toString())
                    .build();
        }

        return response;
    }
}