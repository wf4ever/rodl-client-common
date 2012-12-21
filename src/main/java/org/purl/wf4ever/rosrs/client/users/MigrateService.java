package org.purl.wf4ever.rosrs.client.users;

import java.net.URI;

import com.sun.jersey.api.client.Client;

/**
 * Class helping in migrating the software from ROSR 5 to 6.
 * 
 * @author piotrekhol
 * 
 */
public final class MigrateService {

    /**
     * Private constructor.
     */
    private MigrateService() {
        //nope
    }


    /**
     * Change all uses of one user URI into another.
     * 
     * @param rodlURI
     *            RODL URI
     * @param oldURI
     *            the URI to be replaced
     * @param newURI
     *            the URI to replace with
     * @return message from RODL
     */
    public static String updateUserURI(URI rodlURI, URI oldURI, URI newURI) {
        Client client = Client.create();
        return client.resource(rodlURI.toString()).path("userUpdate").type("text/plain")
                .post(String.class, oldURI.toString() + "\r\n" + newURI.toString());
    }
}
