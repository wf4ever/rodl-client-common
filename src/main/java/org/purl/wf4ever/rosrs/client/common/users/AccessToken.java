/**
 * 
 */
package org.purl.wf4ever.rosrs.client.common.users;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity that represents an OAuth 2.0 access token received from RODL.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "access-token")
public class AccessToken implements Serializable {

    /** id. */
    private static final long serialVersionUID = 8724845005623981779L;

    /** the access token. */
    private String token;

    /** RODL. */
    private OAuthClient client;

    /** Token creation date. */
    private Date created;

    /** Token last used date. */
    private Date lastUsed;


    @XmlElement
    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    @XmlElement
    public OAuthClient getClient() {
        return client;
    }


    public void setClient(OAuthClient client) {
        this.client = client;
    }


    @XmlElement
    public Date getCreated() {
        return created;
    }


    public void setCreated(Date created) {
        this.created = created;
    }


    @XmlElement
    public Date getLastUsed() {
        return lastUsed;
    }


    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

}
