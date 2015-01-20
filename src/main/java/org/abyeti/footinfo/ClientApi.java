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
        System.out.println(entString);
        JSONObject jsonData = new JSONObject(entString);

        FootDB db = new FootDB();
        if(jsonData.getString("ACTION").equals("ADD_TEAM")) {
            String teamName = jsonData.getString("TEAM_NAME");
            String[] players = jsonData.getString("PLAYERS").split(";");
            db.addTeam(teamName, players);
        }
        return Response.ok().entity("{'potato' : 'veg3t4b3l'}").build();
    }
}