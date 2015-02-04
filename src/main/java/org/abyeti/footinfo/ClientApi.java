package org.abyeti.footinfo;

import com.unboundid.ldap.sdk.*;
import org.abyeti.footinfo.db.DataDB;
import org.abyeti.footinfo.db.FootDB;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.SocketFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/api")
public class ClientApi {

    private final String defaultParentDn = "dc=example,dc=com";
    private final String loginDn = "uid=admin,ou=system";
    private final String password = "secret";
    private final int ldapPort = 10389;
    private final String ldapDomain = "localhost";
    private SocketFactory sfact = null;

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
            DataDB.addFoul(foul.commited_by, foul.foul_on, new DateTime().toString(), foul.match_id);
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
            DataDB.addPlayerCard(card.awarded_to, card.card_type, card.match_id);
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
            DataDB.addGoal(goal.goal_type, goal.scored_by, new DateTime().toString(), goal.match_id, goal.team_id);
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
            JSONArray stats = DataDB.getPlayerStats(playerData.player_id);
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
    @Path("/test")
    public void testEndpoint() {
        try {
            FootDB db = new FootDB();
            System.out.println(db.getStartTime("7d67eeee-9a63-4356-b2af-5921fdd5698f"));
            DataDB.getTopFeed();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/authAdmin")
    public Response authAdmin(ApiDataModels.AdminAuth cred) {
        try {
            if(authUser(cred.password, cred.username, "footInfoAdmins")) {
                return Response.status(200).entity(String.format("{\"status\" : \"ok\", \"auth_code\" : \"%s\"}", getAuthCode())).build();
            }
            return Response.status(200).entity("{\"status\" : \"not_authenticated\", \"auth_code\" : \"%s\"}").build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).entity("{\"status\" : \"skrew u\"}").build();
        }
    }

    public boolean authUser(String password, String uid, String orgName) {
        try {
            LDAPConnection connection = getConnection();
            final String orgDn = String.format("o=%s,dc=example,dc=com", orgName);
            System.out.println(orgDn);


            SearchResult res = connection.search(new SearchRequest(
                    orgDn, SearchScope.SUB, Filter.createEqualityFilter("userPassword", password)
            ));

            for(SearchResultEntry ent : res.getSearchEntries()){
                System.out.println(ent.toString());
            }
            System.out.println(res.getEntryCount());

            if(res.getEntryCount() == 1)
                return true;
            else
                return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LDAPConnection getConnection()  throws Exception {
        if(sfact == null) {
            sfact = SocketFactory.getDefault();
            System.out.println("Creating socket factory!");
        }

        LDAPConnection connection = new LDAPConnection(sfact, ldapDomain, ldapPort);
        BindResult result = connection.bind(new SimpleBindRequest(loginDn, password));

        if(result.getResultCode() == ResultCode.INVALID_CREDENTIALS)
            return null;
        else
            return connection;
    }

    public boolean verifyAuthCodes() {
        return false;
    }

    public String getAuthCode() {
        return null;
    }

}