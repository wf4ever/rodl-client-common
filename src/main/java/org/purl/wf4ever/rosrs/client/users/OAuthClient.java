/**
 * 
 */
package org.purl.wf4ever.rosrs.client.users;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an OAuth client provided by RODL.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "client")
public class OAuthClient implements Serializable {

    /** id. */
    private static final long serialVersionUID = -396954799246175590L;

    /** Out Of Band, a non-web client. */
    public static final String OOB = "OOB";

    /** client id. */
    private String clientId;

    /** client name. */
    private String name;

    /** client redirection URI. */
    private String redirectionURI;


    /**
     * Default constructor.
     */
    public OAuthClient() {
    }


    /**
     * Constructor.
     * 
     * @param clientId
     *            client id
     * @param name
     *            client name
     * @param redirectionURI
     *            redirection URI
     */
    public OAuthClient(String clientId, String name, String redirectionURI) {
        this.clientId = clientId;
        this.name = name;
        this.redirectionURI = redirectionURI;
    }


    @XmlElement
    public String getClientId() {
        return clientId;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    @XmlElement
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @XmlElement
    public String getRedirectionURI() {
        return redirectionURI;
    }


    public void setRedirectionURI(String redirectionURI) {
        this.redirectionURI = redirectionURI;
    }


    @Override
    public String toString() {
        return getName();
    }
}
