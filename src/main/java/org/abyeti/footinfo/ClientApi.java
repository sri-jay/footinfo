package org.abyeti.footinfo;

import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class ClientApi {
    @POST
    @Path("/post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adminLogin(String entString) throws Exception {
        System.out.println(entString);
        JSONObject jsonData = new JSONObject(entString);
        return Response
            .status(200)
            .entity(jsonData.toString())
            .build();
    }
}