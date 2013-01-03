package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;

import org.joda.time.DateTime;

/**
 * Base of all resources.
 * 
 * @author piotrekhol
 * 
 */
public class Thing implements Serializable {

    /** id. */
    private static final long serialVersionUID = -6086301275622387040L;

    /** resource URI. */
    protected URI uri;

    /** creator URI. */
    protected URI creator;

    /** creation date. */
    protected DateTime created;


    /**
     * Constructor.
     * 
     * @param uri
     *            resource URI
     * @param created
     *            creation date
     * @param creator
     *            creator URI
     */
    public Thing(URI uri, URI creator, DateTime created) {
        this.uri = uri;
        this.creator = creator;
        this.created = created;
    }


    public URI getUri() {
        return uri;
    }


    public URI getCreator() {
        return creator;
    }


    public DateTime getCreated() {
        return created;
    }

}
