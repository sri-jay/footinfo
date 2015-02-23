package org.abyeti.footinfo;

import org.abyeti.footinfo.db.DataDB;
import org.abyeti.footinfo.db.FootDB;
import org.abyeti.footinfo.db.UserDB;
import org.joda.time.DateTime;
import org.json.JSONArray;
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
    public Response addTeam(ApiDataModels.AddTeamModel teamData) {
        Response resp = null;
        try {
            FootDB db = new FootDB();

            db.addTeam(teamData.team_name, teamData.players.split(";"));
            resp = Response.status(200).entity("{\"STATUS\" : \"SUCCEEDED\"}").build();

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
    public Response createGame(ApiDataModels.CreateGameModel gameData) {
        JSONObject responseData = new JSONObject();

        Response response = Response
                .status(200)
                .entity(responseData.append("STATUS", "SUCCEEDED").toString())
                .build();


        try {
            FootDB db = new FootDB();
            if(!db.createGame(gameData.teamA, gameData.teamB)) {
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

    @GET
    @Path("/getGameStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllGameStatus() {
        JSONObject gameSatuses = new JSONObject();

        try {
            FootDB db = new FootDB();
            gameSatuses.accumulate("game_statuses", db.getGameStatuses());
            gameSatuses.accumulate("status", "suceeded");
            return Response
                    .status(200)
                    .entity(gameSatuses.toString())
                    .build();
        }
        catch(Exception e) {
            e.printStackTrace();
            return Response
                    .status(500)
                    .entity(new JSONObject().append("status", "failed").toString())
                    .build();
        }
    }

    @GET
    @Path("/getAllTeams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeam(ApiDataModels.GetTeam teamData) {
        JSONObject data = new JSONObject();
        try {
            FootDB db = new FootDB();
            data.accumulate("teams", db.getAllClubs());
            data.accumulate("status", "succeeded");
            return Response.status(200).entity(data.toString()).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/finishGame")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response stopGame(ApiDataModels.FinishGameModel gameData) {
        try {
            FootDB db = new FootDB();
            if(db.stopGame(gameData.match_id)) {
                return Response.status(200).entity("{\"status\" : \"succeeded\" }").build();
            }
            else
                return Response.status(200).entity("{\"status\" : \"failed\" }").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\" }").build();
        }
    }

    @POST
    @Path("/getTeamData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPlayerData(ApiDataModels.GetTeam teamData) {
        JSONObject response = new JSONObject();

        try {
            FootDB db = new FootDB();
            System.out.println(teamData);
            response.accumulate("teamData", db.getTeamData(teamData.team_id));
            response.accumulate("status" , "{\"status\" : \"succeeded\"}");
            return Response.status(200).entity(response.toString()).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(400).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/getGameData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGameData(ApiDataModels.GameData game) {
        Response res = null;
        try {
            FootDB db = new FootDB();
            res = Response.status(200).entity(db.getGameData(game.match_id).toString()).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            res = Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
        return res;
    }

    @POST
    @Path("/playerFoul")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response playerFoul(ApiDataModels.PlayerFoul foul) {
        try {
            new DataDB().addFoul(foul.committed_by, foul.foul_on, new DateTime().toString(), foul.match_id);
            return Response.status(200).entity("{\"status\" : \"succeeded\"}").build();
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/playerCard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response playerCard(ApiDataModels.PlayerCard card) {
        try {
            new DataDB().addPlayerCard(card.awarded_to, card.card_type, card.match_id);
            return Response.status(200).entity("{\"status\" : \"succeeded\"}").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/matchGoal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchGoal(ApiDataModels.MatchGoal goal) {
        try {
            new DataDB().addGoal(goal.goal_type, goal.scored_by, new DateTime().toString(), goal.match_id, goal.team_id);
            return Response.status(200).entity("{\"status\" : \"succeeded\"}").build();
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/getPlayerStats")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPlayerStats(ApiDataModels.PlayerData playerData) {
        try {
            JSONArray stats = new DataDB().getPlayerStats(playerData.player_id);
            return Response
                    .status(200)
                    .entity(stats.toString())
                    .build();
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/authAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authAdmin(ApiDataModels.AdminAuth cred) {
        try {

            if(UserDB.authUser(cred.password, cred.username, "footInfoAdmins")) {
                return Response.status(200).entity(String.format("{\"status\" : \"ok\", \"auth_code\" : \"%s\"}", UserDB.getAuthCode())).build();
            }
            return Response.status(200).entity("{\"status\" : \"not_authenticated\", \"auth_code\" : \"%s\"}").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).entity("{\"status\" : \"skrew u\"}").build();
        }
    }

    @GET
    @Path("/getTopFeed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopFeed() {
        try {
            return Response.status(200).entity(new DataDB().getTopFeed().toString()).build();
        }

        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"fuq u\"}").build();
        }
    }

    @POST
    @Path("/getMatchFeed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMatchFeed(ApiDataModels.GameData game) {
        try {
            return Response.status(200).entity(new DataDB().getMatchFeed(game.match_id).toString()).build();
        }

        catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"status\" : \"fuq u\"}").build();
        }
    }

    @POST
    @Path("/addSubscriber")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeEmail(ApiDataModels.Subscriber sub) {
        try {
            UserDB.addSubscriber(sub.email_id);
            return Response.status(200).entity("{\"status\" : \"ok\"}").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).entity("{\"status\" : \"failed\"}").build();
        }
    }

    @POST
    @Path("/archiveMatch")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMatch(ApiDataModels.GameData gameData) {
        try {
            DataDB db = new DataDB();
            db.archiveMatch(gameData.match_id);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/deleteArchives")
    public void deleteArchives() {
        try {
            new DataDB().reapArchivedData();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
