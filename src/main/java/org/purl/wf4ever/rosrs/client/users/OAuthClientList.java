package org.purl.wf4ever.rosrs.client.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of {@link OAuthClient}.
 * 
 * @author piotrekhol
 * 
 */
@XmlRootElement(name = "clients")
public class OAuthClientList implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1476500812402240650L;

    /** OAuth clients. */
    protected List<OAuthClient> list = new ArrayList<OAuthClient>();


    /**
     * Default constructor.
     */
    public OAuthClientList() {
    }


    /**
     * Constructor.
     * 
     * @param list
     *            OAuth clients
     */
    public OAuthClientList(List<OAuthClient> list) {
        this.list = list;
    }


    @XmlElement(name = "client")
    public List<OAuthClient> getList() {
        return list;
    }
}
