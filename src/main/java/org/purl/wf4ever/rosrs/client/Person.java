package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;

/**
 * A rough representation of a foaf:Person.
 * 
 * @author piotrekhol
 * 
 */
public class Person implements Serializable {

    /** id. */
    private static final long serialVersionUID = -6956273744325435068L;

    /** URI used in metadata. */
    private final URI uri;

    /** Human-friendly name. */
    private final String name;


    /**
     * Constructor.
     * 
     * @param uri
     *            URI used in metadata
     * @param name
     *            human-friendly name
     */
    public Person(URI uri, String name) {
        this.uri = uri;
        this.name = name;
    }


    public URI getUri() {
        return uri;
    }


    public String getName() {
        return name;
    }

}
