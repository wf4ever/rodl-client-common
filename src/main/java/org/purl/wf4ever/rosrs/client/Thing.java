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
    protected final URI uri;

    /** last segment of URI or full URI if no path exists. */
    protected final String name;

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
        this.name = calculateName();
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


    /**
     * Returns the last segment of the resource path, or the whole URI if has no path.
     * 
     * @return name or null
     */
    public String calculateName() {
        if (uri == null) {
            return null;
        }
        if (uri.getPath().isEmpty() || uri.getPath().equals("/")) {
            return uri.toString();
        }
        String[] segments = uri.getPath().split("/");
        String name2 = segments[segments.length - 1];
        if (uri.getPath().endsWith("/")) {
            name2 = name2.concat("/");
        }
        return name2;
    }


    public String getName() {
        return name;
    }

}
