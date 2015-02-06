package org.abyeti.footinfo.db;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.LDAPTestUtils;
import org.glassfish.jersey.client.ClientResponse;
import scala.xml.Equality;

import javax.net.SocketFactory;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

public class UserDB {

    private static final String defaultParentDn = "dc=example,dc=com";
    private static final String loginDn = "uid=admin,ou=system";
    private static final String password = "secret";
    private static final int ldapPort = 10389;
    private static final String ldapDomain = "localhost";
    private static SocketFactory sfact = null;

    public static boolean authUser(String password, String uid, String orgName) {
        try {
            LDAPConnection connection = getConnection();
            final String orgDn = String.format("o=%s,dc=example,dc=com", orgName);
            System.out.println(orgDn);


            SearchResult res = connection.search(new SearchRequest(
                    orgDn, SearchScope.SUB, Filter.createEqualityFilter("uid", password)
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

    public static LDAPConnection getConnection()  throws Exception {
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

    public static boolean verifyAuthCodes()throws Exception  {
        return false;
    }

    public static String getAuthCode() throws Exception  {
        return null;
    }

    public static  String addSubscriber(String emailId)  throws Exception {
        String uid = UUID.randomUUID().toString();
        LDAPConnection connecton = getConnection();
        Entry ent = new Entry(String.format("email=%s,o=subscribers,%s", emailId,defaultParentDn));
        ent.addAttribute("objectClass", "account");
        ent.addAttribute("objectClass", "extensibleObject");
        ent.setAttribute("uid", UUID.randomUUID().toString());

        connecton.add(new AddRequest(
            ent
        ));

        connecton.close();
        return uid;
    }

    public static void notifySubscribers(String title, String text) throws Exception {
        LDAPConnection connection = getConnection();
        SearchResult res = connection.search(
                new SearchRequest(
                        "o=subscribers,dc=example,dc=com",
                        SearchScope.SUB,
                        Filter.createPresenceFilter("email"))
        );

        for(SearchResultEntry ent : res.getSearchEntries()) {
            System.out.println(String.format("Sending email to : %s", ent.getAttribute("email").getValue()));
            sendMail(ent.getAttribute("email").getValue(), title, text);
        }

        connection.close();
    }

    public static ClientResponse sendMail(String emailId, String sub, String body) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-095bfbe720132786f8be12f597eecdb0"));
        WebResource webResource = client.resource("https://api.mailgun.net/v2/sandbox8d0f303756fb44e8973fe35613b2f643.mailgun.org/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "Mailgun Sandbox <postmaster@sandbox8d0f303756fb44e8973fe35613b2f643.mailgun.org>");
        formData.add("to", String.format("%s <%s>", emailId, emailId));
        formData.add("subject", sub);
        formData.add("text", body);
        return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).entity(formData).post(ClientResponse.class);
    }
}
